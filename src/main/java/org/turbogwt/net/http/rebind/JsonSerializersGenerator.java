package org.turbogwt.net.http.rebind;

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
import java.util.Iterator;

import org.turbogwt.net.http.client.serialization.SerializationContext;
import org.turbogwt.net.http.client.serialization.Serializer;
import org.turbogwt.net.http.shared.serialization.JsonSerialize;

/**
 * Generator for {@link org.turbogwt.net.http.shared.serialization.JsonSerialize} annotated types.
 *
 * @author Danilo Reinert
 */
public class JsonSerializersGenerator extends Generator {

    @Override
    public String generate(TreeLogger logger, GeneratorContext ctx, String typeName) throws UnableToCompleteException {
        TypeOracle typeOracle = ctx.getTypeOracle();
        assert (typeOracle != null);

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

        TreeLogger typeLogger = logger.branch(TreeLogger.ALL, "Generating serializers with Gwt Jackson...", null);
        final SourceWriter sourceWriter = getSourceWriter(typeLogger, ctx, intfType);

        if (sourceWriter != null) {
            sourceWriter.println();

            final ArrayList<String> serializers = new ArrayList<>();
            for (JClassType type : typeOracle.getTypes()) {
                JsonSerialize annotation = type.getAnnotation(JsonSerialize.class);
                if (annotation != null) {
                    serializers.add(generateSerializer(sourceWriter, type, annotation));
                }
            }

            generateFields(sourceWriter);
            generateConstructor(sourceWriter, serializers);
            generateIteratorMethod(sourceWriter);

            sourceWriter.commit(typeLogger);
        }

        return typeName + "Impl";
    }

    private String getTypeSimpleName() {
        return "GeneratedJsonSerializersImpl";
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
                        ObjectWriter.class.getCanonicalName(), GWT.class.getCanonicalName(),
                        ArrayList.class.getCanonicalName(), Collection.class.getCanonicalName(),
                        Iterator.class.getCanonicalName(), SerializationContext.class.getCanonicalName(),
                        Serializer.class.getCanonicalName()};
        for (String imp : imports) {
            composerFactory.addImport(imp);
        }

        composerFactory.addImplementedInterface(intfType.getErasedType().getQualifiedSourceName());

        return composerFactory.createSourceWriter(ctx, printWriter);
    }

    /**
     * Create the serializer and return the field name.
     */
    private String generateSerializer(SourceWriter srcWriter, JClassType type, JsonSerialize annotation) {
        final String qualifiedSourceName = type.getQualifiedSourceName();

        final String qualifiedCamelCaseFieldName = replaceDotByUpperCase(qualifiedSourceName);
        final String qualifiedCamelCaseTypeName = Character.toUpperCase(qualifiedCamelCaseFieldName.charAt(0)) +
                qualifiedCamelCaseFieldName.substring(1);

        final String singleWriterType = qualifiedCamelCaseTypeName + "Writer";
        final String arrayWriterType = qualifiedCamelCaseTypeName + "ArrayWriter";

        // interfaces extending Gwt Jackson
        srcWriter.println("interface %s extends ObjectWriter<%s> {}", singleWriterType, qualifiedSourceName);
        srcWriter.println("interface %s extends ObjectWriter<Collection<%s>> {}", arrayWriterType, qualifiedSourceName);
        srcWriter.println();

        final String singleWriterField = qualifiedCamelCaseFieldName + "Writer";
        final String arrayWriterField = qualifiedCamelCaseFieldName + "ArrayWriter";

        // fields creating interfaces
        srcWriter.println("private final %s %s = GWT.create(%s.class);", singleWriterType, singleWriterField,
                singleWriterType);
        srcWriter.println("private final %s %s = GWT.create(%s.class);", arrayWriterType, arrayWriterField,
                arrayWriterType);
        srcWriter.println();

        final String serializerField = qualifiedCamelCaseFieldName + "Serializer";
        final String serializerType = "Serializer<" + qualifiedSourceName + ">";

        // serializer field as anonymous class
        srcWriter.println("private final %s %s = new %s() {", serializerType, serializerField, serializerType);
        srcWriter.println();

        // handledType
        srcWriter.println("    @Override");
        srcWriter.println("    public Class<%s> handledType() {", qualifiedSourceName);
        srcWriter.println("        return %s.class;", qualifiedSourceName);
        srcWriter.println("    }");
        srcWriter.println();

        // contentyType
        srcWriter.println("    @Override");
        srcWriter.println("    public String[] contentType() {");
        srcWriter.println("        return new String[]{ %s };", asStringCsv(annotation.value()));
        srcWriter.println("    }");
        srcWriter.println();

        // serialize
        srcWriter.println("    @Override");
        srcWriter.println("    public String serialize(%s o, SerializationContext context) {", qualifiedSourceName);
        srcWriter.println("        return %s.write(o);", singleWriterField);
        srcWriter.println("    }");
        srcWriter.println();

        // serializeFromCollection
        srcWriter.println("    @Override");
        srcWriter.println("    public String serializeFromCollection(Collection<%s> c, SerializationContext context) {",
                qualifiedSourceName);
        srcWriter.println("        return %s.write(c);", arrayWriterField);
        srcWriter.println("    }");

        // end anonymous class
        srcWriter.println("};");
        srcWriter.println();

        return serializerField;
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
        srcWriter.println("private final ArrayList<Serializer<?>> serializerList = new ArrayList<>();");
        srcWriter.println();
    }

    private void generateConstructor(SourceWriter srcWriter, ArrayList<String> serializers) {
        srcWriter.println("public GeneratedJsonSerializersImpl() {");
        for (String serializer : serializers) {
            srcWriter.println("    serializerList.add(%s);", serializer);
        }
        srcWriter.println("}");
        srcWriter.println();
    }

    private void generateIteratorMethod(SourceWriter srcWriter) {
        // Initialize a field with binary name of the remote service interface
        srcWriter.println("@Override");
        srcWriter.println("public Iterator<Serializer<?>> iterator() {");
        srcWriter.println("    return serializerList.iterator();");
        srcWriter.println("}");
        srcWriter.println();
    }
}
