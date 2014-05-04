Turbo GWT (*TurboG*) HTTP [![Build Status](https://travis-ci.org/growbit/turbogwt-http.svg?branch=master)](https://travis-ci.org/growbit/turbogwt-http)
==
[**Turbo GWT**](http://github.com/growbit/turbogwt) is a suite of libs intended to speed up development of GWT applications. It aims to promote a fluent and enjoyable programming.

**Turbo GWT HTTP** is a convenient API for managing client-server communication and performing requests fluently.

## Highlights

* `GET`, `POST`, `PUT`, `DELETE` and `HEAD` requests
* All basic components extended from GWT HTTP and RPC APIs
* Easy building of target URI with no string manipulation
* Customizable multi-valued param composition
* Nice support to form params
* Native Basic Authentication support
* Customizable timeout
* Customizable callback execution based on server response
* Handy header construction and application
* Request and Response filtering (enhancement)
* Customizable `ServerConnection` implementation (default directs to XMLHttpRequest)
* Automatic JSON parsing into Overlay types
* Easy De/Serialization and support to different content-types (by pattern matching)

## Quick Start

TurboG proposes a new fluent way of making http requests. It fits better the REST style communication. 
Just look how simple you can get a book from server:

```java
Request request = requestor.request(Void.class, Book.class)
        .path("server").segment("books").segment(1)
        .get(new AsyncCallback<Book>() {
            @Override
            public void onFailure(Throwable caught) {
        
            }
        
            @Override
            public void onSuccess(Book result) {
                Window.alert("My book title: " + result.getTitle());
            }
});
```

For serializing/deserializing this object you just need to create this simple SerDes.

```java 
public class BookJsonSerdes extends JsonObjectSerdes<Book> {

    @Override
    public Book readJson(JsonRecordReader reader, DeserializationContext context) {
        return new Book(reader.readInteger("id"),
                reader.readString("title"),
                reader.readString("author"));
    }

    @Override
    public void writeJson(Book book, JsonRecordWriter writer, SerializationContext context) {
        writer.writeInt("id", book.getId())
                .writeString("title", book.getTitle())
                .writeString("author", book.getAuthor());
    }
}
```

One configuration step: just remember to register your SerDes in the [Requestor](#requestor).
<br />
If you are using *Overlays*, then you don't need any SerDes, *serialization/deserialization is automatic*!

Doing a POST is as simple as:

```java 
Request request = requestor.request(Book.class, Void.class).path("server").segment("books")
        .post(new Book(1, "My Title", "My Author"), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {
                Window.alert("POST done!");
            }
        });
```

If you are too lazy, Requestor provides **shortcut methods** to perform requests with only one method call. 
The above could be done like this:

```java 
Request request = requestor.post("/server/books", Book.class, new Book(1, "My Title", "My Author"), Void.class, 
        new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Void result) {
                Window.alert("POST done!");
            }
        });
```

### How do I retrieve a collection instead of a single object?
TurboG HTTP checks the type in compile time.
 It resorts to Java Generics to differentiate between single object and collection of the object.

So if you want to retrieve a collection of T in your response, you can use a [ListAsyncCallback<T>](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/ListAsyncCallback.html)
 (or [SetAsyncCallback<T>](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/SetAsyncCallback.html)), instead of the AsyncCallback<T>.

```java
requestor.request(Void.class, Book.class).path("server").segment("books").get(new ListAsyncCallback<Book>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<Book> result) {
                // w00t! A list of books!
            }
        });
```

Both [ListAsyncCallback](https://github.com/growbit/turbogwt-http/blob/master/src/main/java/org/turbogwt/net/http/ListAsyncCallback.java) and [SetAsyncCallback](https://github.com/growbit/turbogwt-http/blob/master/src/main/java/org/turbogwt/net/http/SetAsyncCallback.java) inherits from [ContainerAsyncCallback](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/ContainerAsyncCallback.html), which requires its subclasses to
 inform the collection type they accumulate the result.

When deserializing, the Deserializer retrieves an instance of the collection from the CollectionFactoryManager.

You can create custom Factories of Collection types, register them in the Requestor,
 and use a custom ContainerCallback of this type.
 
### Customizable callback execution
With FluentRequests you can set callbacks for specific responses, with specificity priority.

```java 
Request request = requestor.request().path(uri)
        .on(404, new SingleCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                Window.alert("Hey! The resource does not exist!");
            }
        })
        .on(20, new SingleCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                Window.alert("200, 201, ..., 209");
            }
        })
        .on(2, new SingleCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                Window.alert("210, 211, ..., 299");
                // 200 - 209 responses won't reach here because you set a callback for the 20 dozen.
            }
        })
        .get(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Some bad thing happened!");
            }
            @Override
            public void onSuccess(Void result) {
                // Won't reach here. 
                // Only 200-299 responses call onSuccess, and you have already set callbacks for those.
            }
        });
```
 
### Basic Authentication
FluentRequest supports setting user and password.

```java
requestor.request(Void.class, String.class)
        .path("hello")
        .user(username).password(pwd)
        .get(new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(String result) {
                Window.alert("Hello " + result + "!");
            }
        });
```

### Sending FORM data
TurboG HTTP provides two handful classes for dealing with Forms: *FormParam* and *FormData* (a collection of FormParams with a nice builder). You can use both of them to make a form post.

```java
FormData formData = FormData.builder().put("name", "John Doe").put("array", 1, 2.5).build();

Request request = requestor.request(FormParam.class, Void.class)
        .path(uri)
        .contentType("application/x-www-form-urlencoded")
        .post(formData); // We optionally set no callback, disregarding the server response
```
 
### Requestor
[Requestor](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/Requestor.html) is the main component of TurboG HTTP. It is responsible for managing the various aggregate components for the requests (as SerdesManager, FilterManager, CollectionFactoryManager) and create [FluentRequests](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/FluentRequest.html) supporting those. It should be used as a singleton over all your application.

### JSON, XML and whatever living together
The [Serializer](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/serialization/Serializer.html)
 and [Deserializer](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/serialization/Deserializer.html)
 interfaces requires you to inform the *content-type patterns* they handle.
 After registering them at the Requestor, when requesting it will look for the most specific Serializer for serializing
 outgoing data and the most specific Deserializer for deserializing incoming data.

The tests shows an example (see [this test](https://github.com/growbit/turbogwt-http/blob/master/src/test/java/org/turbogwt/net/http/MultipleSerdesByClassTest.java) and [the SerDes](https://github.com/growbit/turbogwt-http/tree/master/src/test/java/org/turbogwt/net/http/books))
 of having both SerDes for XML and JSON related to the same type.

Notice [FluentRequest](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/FluentRequest.html) (returned by Requestor) enables you to specify the exact content-type you want to serialize your
 outgoing data (FluentRequest#content-type(String)) and the content-type you want to receive from the server
 (FluentRequest#accept(String) or FluentRequest#accept(AcceptHeader)). Both default values are "application/json".
 
An abstract SerDes implementation for JSON would be like:

```
public abstract class JsonSerdes<T> implements Serdes<T> {

    /**
     * Method for accessing type of the Object this deserializer can handle.
     *
     * @return The class handled by this serializer
     */
    abstract Class<T> handledType();

    /**
     * Informs the content type this deserializer handle.
     *
     * @return The content type handled by this deserializer.
     */
    @Override
    public String[] accept() {
        return new String[] { "application/json", "application/javascript", "*/json", "*/json+*, "*/*+json" };
    }

    /**
     * Informs the content type this serializer serializes.
     *
     * @return The content type serialized.
     */
    @Override
    public String[] contentType() {
        return new String[] { "application/json", "application/javascript", "*/json", "*/json+*, "*/*+json" };
    }
    
    // ... (omitted)
}
    
```


### Multiple value parameters
There's a feature called [MultipleParamStrategy](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/MultipleParamStrategy.html) that defines the way params with more than one value should be composed
 when building a URL or a FormParam. There are two strategies provided: RepeatedParam and CommaSeparated. The former
 repeats the param name with each value - this is the default and most practiced strategy -, the latter puts only
 once the parameter name and join the values separated by comma.

### Request/Response filters
You can easily enhance all your requests with [RequestFilter](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/RequestFilter.html) and your responses with [ResponseFilter](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/ResponseFilter.html).
 Suppose you want to add a custom authentication header in all requests after the user successfully authenticated.
 Just register a RequestFilter in the Requestor that performs this operation.
 If latter you want do undo this registration, you can hold the Registration instance returned at the time of
 registering and execute Registration#removeHandler().

Ps: [Registration](http://growbit.github.io/turbogwt-core/javadoc/apidocs/org/turbogwt/core/util/Registration.html) is a TurboG Core class. Inherits from HandlerRegistration,
 but is essentially a general purpose registration element.
 All register* methods in Requestor return a registration instance, enabling the latter deregistration.

### Easier header construction
TurboG HTTP provides Header classes facilitating complex header construction.
 E.g., you can create a [QualityFactorHeader](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/QualityFactorHeader.html) and pass it to your request.

### Extensible design
All Requests are created by an underlying abstraction called [Server](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/Server.html). This Server interface is analogous to the JDBC Datasource and provides a new [ServerConnection](http://growbit.github.io/turbogwt-http/javadoc/apidocs/org/turbogwt/net/http/ServerConnection.html) by calling getConnection(). This design allows you to determine how you want to communicate with your Server over all your application.

E.g., suppose you are creating a mobile application and want to prevent data loss by poor connection. You can create a new implementation of Server that stores the data on the browser's phone if no internet connection is availble, and sync the data when the signal is back.

The default implementation of Server ([ServerImpl](https://github.com/growbit/turbogwt-http/blob/master/src/main/java/org/turbogwt/net/http/ServerImpl.java)) creates the [ServerConnectionImpl](https://github.com/growbit/turbogwt-http/blob/master/src/main/java/org/turbogwt/net/http/ServerConnectionImpl.java) (default implementation of ServerConnection), which performs the communication by directly creating a request using RequestBuilder and sending it. The binding is done via DefferedBinding. 

### Tests
Take a look at the [tests](https://github.com/growbit/turbogwt-http/tree/master/src/test/java/org/turbogwt/net/http) for more examples.

## Documentation
* [Javadocs](http://growbit.github.io/turbogwt-http/javadoc/apidocs/index.html)

## Community
* [Turbo GWT Google Group](http://groups.google.com/d/forum/turbogwt) - Share ideas and ask for help.

## Downloads
Turbo GWT HTTP is currently available at maven central.

### Maven
```
<dependency>
    <groupId>org.turbogwt.net</groupId>
    <artifactId>turbogwt-http</artifactId>
    <version>0.3.0</version>
</dependency>

<!-- Required dependency -->
<dependency>
    <groupId>org.turbogwt.core</groupId>
    <artifactId>turbogwt-core</artifactId>
    <version>0.3.0</version>
</dependency>
```

## License
Turbo GWT HTTP is freely distributable under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
