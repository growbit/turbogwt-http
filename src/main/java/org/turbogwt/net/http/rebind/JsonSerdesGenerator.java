/*
 * Copyright 2014 Grow Bit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.turbogwt.net.http.rebind;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.github.nmorel.gwtjackson.client.ObjectReader;
import com.github.nmorel.gwtjackson.client.ObjectWriter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JPackage;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.turbogwt.core.util.client.Overlays;
import org.turbogwt.net.http.client.serialization.DeserializationContext;
import org.turbogwt.net.http.client.serialization.Deserializer;
import org.turbogwt.net.http.client.serialization.JsonObjectSerdes;
import org.turbogwt.net.http.client.serialization.JsonRecordReader;
import org.turbogwt.net.http.client.serialization.JsonRecordWriter;
import org.turbogwt.net.http.client.serialization.Serdes;
import org.turbogwt.net.http.client.serialization.SerializationContext;
import org.turbogwt.net.http.client.serialization.Serializer;
import org.turbogwt.net.http.shared.serialization.Json;

/**
 * Generator for {@link org.turbogwt.net.http.shared.serialization.JsonSerialize} annotated types.
 *
 * @author Danilo Reinert
 */
public class JsonSerdesGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx, String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = ctx.getTypeOracle();
        assert typeOracle != null;

        JClassType intfType = typeOracle.findType(typeName);
        if (intfType == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
                    + typeName + "'", null);
            throw new UnableToCompleteException();
        }

        if (intfType.isInterface() == null) {
            logger.log(TreeLogger.ERROR, intfType.getQualifiedSourceName()
                    + " is not an interface", null);
            throw new UnableToCompleteException();
        }

        TreeLogger typeLogger = logger.branch(TreeLogger.ALL, "Generating SerDes powered by Gwt Jackson...", null);
        final SourceWriter sourceWriter = getSourceWriter(typeLogger, ctx, intfType);

        if (sourceWriter != null) {
            sourceWriter.println();

            final ArrayList<String> serdes = new ArrayList<>();
            for (JClassType type : typeOracle.getTypes()) {
                Json annotation = type.getAnnotation(Json.class);
                if (annotation != null) {
                    serdes.add(generateSerdes(sourceWriter, type, annotation));
                }
            }

            generateFields(sourceWriter);
            generateConstructor(sourceWriter, serdes);
            generateGetSerdesListMethod(sourceWriter);

            sourceWriter.commit(typeLogger);
        }

        return typeName + "Impl";
    }

    private String getTypeSimpleName() {
        return "GeneratedJsonSerdesImpl";
    }

    private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx, JClassType intfType) {
        JPackage serviceIntfPkg = intfType.getPackage();
        String packageName = serviceIntfPkg == null ? "" : serviceIntfPkg.getName();
        PrintWriter printWriter = ctx.tryCreate(logger, packageName, getTypeSimpleName());
        if (printWriter == null) {
            return null;
        }

        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, getTypeSimpleName());

        String[] imports =
                new String[] {
                        GWT.class.getCanonicalName(), ObjectMapper.class.getCanonicalName(),
                        ObjectWriter.class.getCanonicalName(), ObjectReader.class.getCanonicalName(),
                        List.class.getCanonicalName(), LinkedList.class.getCanonicalName(),
                        ArrayList.class.getCanonicalName(), Collection.class.getCanonicalName(),
                        Set.class.getCanonicalName(), LinkedHashSet.class.getCanonicalName(),
                        HashSet.class.getCanonicalName(), TreeSet.class.getCanonicalName(),
                        DeserializationContext.class.getCanonicalName(), SerializationContext.class.getCanonicalName(),
                        Deserializer.class.getCanonicalName(), Serializer.class.getCanonicalName(),
                        Serdes.class.getCanonicalName(), JsonObjectSerdes.class.getCanonicalName(),
                        JsonRecordReader.class.getCanonicalName(), JsonRecordWriter.class.getCanonicalName(),
                        Overlays.class.getCanonicalName()};
        for (String imp : imports) {
            composerFactory.addImport(imp);
        }

        composerFactory.addImplementedInterface(intfType.getErasedType().getQualifiedSourceName());

        return composerFactory.createSourceWriter(ctx, printWriter);
    }

    /**
     * Create the serdes and return the field name.
     */
    private String generateSerdes(SourceWriter srcWriter, JClassType type, Json annotation) {
        final String qualifiedSourceName = type.getQualifiedSourceName();

        final String qualifiedCamelCaseFieldName = replaceDotByUpperCase(qualifiedSourceName);
        final String qualifiedCamelCaseTypeName = Character.toUpperCase(qualifiedCamelCaseFieldName.charAt(0)) +
                qualifiedCamelCaseFieldName.substring(1);

        final String singleMapperType = qualifiedCamelCaseTypeName + "Mapper";
        final String arrayListMapperType = qualifiedCamelCaseTypeName + "ArrayListMapper";
        final String linkedListMapperType = qualifiedCamelCaseTypeName + "LinkedListMapper";
        final String hashSetMapperType = qualifiedCamelCaseTypeName + "HashSetMapper";
        final String linkedHashSetMapperType = qualifiedCamelCaseTypeName + "LinkedHashSetMapper";
        final String treeSetMapperType = qualifiedCamelCaseTypeName + "TreeSetMapper";

        // interfaces extending Gwt Jackson
        srcWriter.println("interface %s extends ObjectMapper<%s> {}", singleMapperType, qualifiedSourceName);
        srcWriter.println("interface %s extends ObjectMapper<ArrayList<%s>> {}", arrayListMapperType,
                qualifiedSourceName);
        srcWriter.println("interface %s extends ObjectMapper<LinkedList<%s>> {}", linkedListMapperType,
                qualifiedSourceName);
        srcWriter.println("interface %s extends ObjectMapper<HashSet<%s>> {}", hashSetMapperType, qualifiedSourceName);
        srcWriter.println("interface %s extends ObjectMapper<TreeSet<%s>> {}", treeSetMapperType, qualifiedSourceName);
        srcWriter.println("interface %s extends ObjectMapper<LinkedHashSet<%s>> {}", linkedHashSetMapperType,
                qualifiedSourceName);
        srcWriter.println();

        final String singleMapperField = qualifiedCamelCaseFieldName + "Mapper";
        final String arrayListMapperField = qualifiedCamelCaseFieldName + "ArrayListMapper";
        final String linkedListMapperField = qualifiedCamelCaseFieldName + "LinkedListMapper";
        final String hashSetMapperField = qualifiedCamelCaseFieldName + "HashSetMapper";
        final String linkedHashSetMapperField = qualifiedCamelCaseFieldName + "LinkedHashSetMapper";
        final String treeSetMapperField = qualifiedCamelCaseFieldName + "TreeSetMapper";

        // fields creating interfaces
        srcWriter.println("private final %s %s = GWT.create(%s.class);", singleMapperType, singleMapperField,
                singleMapperType);
        srcWriter.println("private final %s %s = GWT.create(%s.class);", arrayListMapperType, arrayListMapperField,
                arrayListMapperType);
        srcWriter.println("private final %s %s = GWT.create(%s.class);", linkedListMapperType, linkedListMapperField,
                linkedListMapperType);
        srcWriter.println("private final %s %s = GWT.create(%s.class);", hashSetMapperType, hashSetMapperField,
                hashSetMapperType);
        srcWriter.println("private final %s %s = GWT.create(%s.class);", linkedHashSetMapperType,
                linkedHashSetMapperField, linkedHashSetMapperType);
        srcWriter.println("private final %s %s = GWT.create(%s.class);", treeSetMapperType, treeSetMapperField,
                treeSetMapperType);
        srcWriter.println();

        final String serdesField = qualifiedCamelCaseFieldName + "Serdes";
        final String serdesType = "JsonObjectSerdes<" + qualifiedSourceName + ">";

        // serializer field as anonymous class
        srcWriter.println("private final %s %s = new %s(%s.class) {", serdesType, serdesField, serdesType,
                qualifiedSourceName);
        srcWriter.println();

        // static field to content-types
        srcWriter.println("    private final String[] PATTERNS = new String[]{ %s };", asStringCsv(annotation.value()));
        srcWriter.println();

        // readJson
        srcWriter.println("    @Override");
        srcWriter.println("    public %s readJson(JsonRecordReader r, DeserializationContext ctx) {", qualifiedSourceName);
        srcWriter.println("        return %s.read(Overlays.stringify(r));", singleMapperField);
        srcWriter.println("    }");
        srcWriter.println();

        // writeJson
        srcWriter.println("    @Override");
        srcWriter.println("    public void writeJson(%s o, JsonRecordWriter w, SerializationContext ctx) {",
                qualifiedSourceName);
        srcWriter.println("        return;");
        srcWriter.println("    }");
        srcWriter.println();

        // contentType
        srcWriter.println("    @Override");
        srcWriter.println("    public String[] contentType() {");
        srcWriter.println("        return PATTERNS;");
        srcWriter.println("    }");
        srcWriter.println();

        // serialize
        srcWriter.println("    @Override");
        srcWriter.println("    public %s deserialize(String s, DeserializationContext context) {", qualifiedSourceName);
        srcWriter.println("        return %s.read(s);", singleMapperField);
        srcWriter.println("    }");
        srcWriter.println();

        // serializeFromCollection
        srcWriter.println("    @Override");
        srcWriter.println("    public <C extends Collection<%s>> C deserializeAsCollection(Class<C> c, " +
                "String s, DeserializationContext ctx) {", qualifiedSourceName);
        srcWriter.println("        if (c == List.class || c == ArrayList.class || c == Collection.class)");
        srcWriter.println("            return (C) %s.read(s);", arrayListMapperField);
        srcWriter.println("        else if (c == LinkedList.class)");
        srcWriter.println("            return (C) %s.read(s);", linkedListMapperField);
        srcWriter.println("        else if (c == Set.class || c == HashSet.class)");
        srcWriter.println("            return (C) %s.read(s);", hashSetMapperField);
        srcWriter.println("        else if (c == TreeSet.class)");
        srcWriter.println("            return (C) %s.read(s);", treeSetMapperField);
        srcWriter.println("        else if (c == LinkedHashSet.class)");
        srcWriter.println("            return (C) %s.read(s);", linkedHashSetMapperField);
        srcWriter.println("        else");
        srcWriter.println("            return super.deserializeAsCollection(c, s, ctx);");
        srcWriter.println("    }");

        // end anonymous class
        srcWriter.println("};");
        srcWriter.println();

        return serdesField;
    }

    private String asStringCsv(String[] array) {
        StringBuilder result = new StringBuilder();
        for (String s : array) {
            result.append('"').append(s).append('"').append(", ");
        }
        result.replace(result.length() - 2, result.length(), "");
        return result.toString();
    }

    private String replaceDotByUpperCase(String s) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c == '.') {
                result.append(Character.toUpperCase(s.charAt(++i)));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    private void generateFields(SourceWriter srcWriter) {
        // Initialize a field with binary name of the remote service interface
        srcWriter.println("private final ArrayList<Serdes<?>> serdesList = new ArrayList<Serdes<?>>();");
        srcWriter.println();
    }

    private void generateConstructor(SourceWriter srcWriter, ArrayList<String> serdes) {
        srcWriter.println("public GeneratedJsonSerdesImpl() {");
        for (String s : serdes) {
            srcWriter.println("    serdesList.add(%s);", s);
        }
        srcWriter.println("}");
        srcWriter.println();
    }

    private void generateGetSerdesListMethod(SourceWriter srcWriter) {
        srcWriter.println("@Override");
        srcWriter.println("public List<Serdes<?>> getSerdesList() {");
        srcWriter.println("    return serdesList;");
        srcWriter.println("}");
        srcWriter.println();
    }
}
