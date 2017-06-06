# AutoValue: Valid Extension

An extension for Google's [AutoValue][auto] that allow you to use javax validation with `AutoValue` classes.

**Note**: To use it you need `AutoValue` version 1.3

## Usage

Include auto-value-valid in your project dependencies and mark getters for fields that should be validated by javax validation:

```java
@AutoValue 
public abstract class User {
  abstract String id();
  abstract String name();

  @AutoValid
  abstract Address email();
}
```

The extension will generate an implementation of the given abstract method annotated with `@Valid` (only one annotation).

## Download

```xml
<dependency>
  <groupId>com.lgi.auto.value.valid</groupId>
  <artifactId>auto-value-valid</artifactId>
  <version>0.1.0</version>
  <scope>provided</scope>
</dependency>
```

 [auto]: https://github.com/google/auto

Do mind that it's currently uploaded to Liberty Globals private repository
