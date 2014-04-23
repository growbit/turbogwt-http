Turbo GWT (*TurboG*) HTTP
==
**Turbo GWT** is a suite of libs intended to speed up development of GWT applications. It aims to promote a fluent and enjoyable programming.

**Turbo GWT HTTP** is a convenient API for managing/performing your requests.

## Quick Start

TurboG proposes a new fluent way of making http requests. It fits better the REST style communication. 
Just look how simple you can get a book from server:

```java
requestor.request(Void.class, Book.class)
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

One configuration step: just remember to register your SerDes in the Requestor.
<br />
If you are using *Overlays*, then you don't need any SerDes, *serialization/deserialization is automatic*!

Doing a POST is as simple as:

```java 
requestor.request(Book.class, Void.class).path("server").segment("books")
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

If you are too lazy, Requestor provides **shortcut methods** to performing requests with only one method call. 
The above could be done like this:

```java 
requestor.post("/server/books", Book.class, new Book(1, "My Title", "My Author"), Void.class, 
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

With FluentRequests you can also set callbacks for specific responses, with specificity priority.

```java 
requestor.request().path(uri)
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

Posting *Form Data* is like:

```java
FormData formData = FormData.builder().put("name", "John Doe").put("array", 1, 2.5).build();

requestor.request(FormParam.class, Void.class)
        .path(uri)
        .contentType("application/x-www-form-urlencoded")
        .post(formData);
```

### How do I retrieve a collection instead of a single object?
TurboG HTTP checks the type in compile time.
 It resorts to Java Generics to differentiate between single object and collection of the object.

So if you want to retrieve a collection of T in your response, you can use a ListAsyncCallback<T>
 (or SetAsyncCallback<T>), instead of the AsyncCallback<T>.

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

Both ListAsyncCallback and SetAsyncCallback inherits from CollectionAsyncCallback, which requires its subclasses to
 inform the collection type they accumulate the result.

When deserializing, the Deserializer retrieves an instance of the collection from the CollectionFactoryManager.

You can create custom Factories of Collection types, register them in the Requestor,
 and use a custom CollectionCallback of this type.

### JSON, XML and whatever living together
The [Serializer](https://github.com/growbit/turbogwt-http/tree/master/src/main/java/org/turbogwt/net/http/serialization/Serializer.java)
 and [Deserializer](https://github.com/growbit/turbogwt-http/tree/master/src/main/java/org/turbogwt/net/http/serialization/Deserializer.java)
 interfaces requires you to inform the *content-type patterns* they handle.
 After registering them at the Requestor, when requesting it will look for the most specific Serializer for serializing
 outgoing data and the most specific Deserializer for deserializing incoming data.

The tests shows an example ([here](https://github.com/growbit/turbogwt-http/tree/master/src/test/java/org/turbogwt/net/http/books))
 of having both Serdes for XML and JSON related to the same type.

Notice FluentRequest (returned by Requestor) enables you to specify the exact content-type you want to serialize your
 outgoing data (FluentRequest#content-type(String)) and the content-type you want to receive from the server
 (FluentRequest#accept(String) or FluentRequest#accept(AcceptHeader)).

### Multiple value parameters
There's a feature called MultipleValueStrategy that defines the way params with more than one value should be composed
 when building a URL or a FormParam. There are two strategies provided: RepeatedParam and CommaSeparated. The former
 repeats the param name with each value - this is the default and most practiced strategy -, the latter puts only
 once the parameter name and join the values separated by comma.

### Request/Response filters
You can easily enhance all your requests with RequestFilter and your responses with ResponseFilter.
 Suppose you want to add a custom authentication header in all requests after the user successfully authenticated.
 Just register a RequestFilter in the Requestor that performs this operation.
 If latter you want do undo this registration, you can hold the Registration instance returned at the time of
 registering and execute Registration#removeHandler().

Ps: Registration is a TurboG Core class. Inherits from HandlerRegistration,
 but is essentially a general purpose registration element.
 All register* methods in Requestor return a registration instance, enabling the latter deregistration.

### Easier header construction
TurboG HTTP provides Header classes facilitating complex header construction.
 E.g., you can create a QualityFactorHeader and pass it to your request.

### Extensible design
All Requests are created by an underlying abstraction called Server. This Server interface is analogous to the JDBC Datasource and provides a new ServerConnection by calling getConnection(). This design allows you to determine how you want to communicate with your Server over all your application.

E.g., suppose you are creating a mobile application and want to prevent data loss by poor connection. You can create a new implementation of Server that stores the data on the browser's phone if no internet connection is availble, and sync the data when the signal is back.

The default implementation of Server (ServerImpl) creates the ServerConnectionImpl (default implementation of ServerConnection), which performs the communication by directly creating a request using RequestBuilder and sending it. The binding is done via DefferedBinding. 

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
