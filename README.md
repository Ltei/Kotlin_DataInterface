### DataInterface

An annotation to generate different kotlin classes representing a model

this...

````kotlin
@DataInterface
interface Model {
    val id: Long
    val name: String
    val emails: List<String>
    val properties: Map<String, String>
}
````

...will generate this :

````kotlin
fun Model.toMutable(): MutableModel = MutableModel(id, name, emails.toMutableList(),
    properties.toMutableMap())

fun Model.toUnsafe(): UnsafeModel = UnsafeModel(id, name, emails.toMutableList(),
    properties.toMutableMap())

fun Model.debug() {
  println("id=${this.id}\nname=${this.name}\nemails=${this.emails}\nproperties=${this.properties}\n")
}

fun Model(
  id: Long,
  name: String,
  emails: List<String>,
  properties: Map<String, String>
): Model = ModelImpl(id, name, emails, properties)

private class ModelImpl(
  override val id: Long,
  override val name: String,
  override val emails: List<String>,
  override val properties: Map<String, String>
) : Model

class MutableModel(
  override var id: Long,
  override var name: String,
  override var emails: MutableList<String>,
  override var properties: MutableMap<String, String>
) : Model {
  fun toUnsafe(): UnsafeModel = UnsafeModel(id, name, emails, properties)

  fun cloneFrom(obj: Model) {
    this.id = obj.id
    this.name = obj.name
    this.emails = obj.emails.toMutableList()
    this.properties = obj.properties.toMutableMap()
  }

  fun cloneFrom(obj: MutableModel) {
    this.id = obj.id
    this.name = obj.name
    this.emails = obj.emails
    this.properties = obj.properties
  }
}

class UnsafeModel(
  var id: Long? = null,
  var name: String? = null,
  var emails: MutableList<String>? = null,
  var properties: MutableMap<String, String>? = null
) {
  fun toSafeOrNull(): MutableModel? {
    val id = this.id
    val name = this.name
    val emails = this.emails
    val properties = this.properties

    return if (id != null && name != null && emails != null && properties != null) {
       MutableModel(id, name, emails, properties)
    } else null
  }

  fun cloneFrom(obj: Model) {
    this.id = obj.id
    this.name = obj.name
    this.emails = obj.emails.toMutableList()
    this.properties = obj.properties.toMutableMap()
  }

  fun cloneFrom(obj: MutableModel) {
    this.id = obj.id
    this.name = obj.name
    this.emails = obj.emails
    this.properties = obj.properties
  }

  fun cloneFrom(obj: UnsafeModel) {
    this.id = obj.id
    this.name = obj.name
    this.emails = obj.emails
    this.properties = obj.properties
  }
}
````

### How to use

In your build.gradle :

````groovy
apply plugin: 'kotlin-kapt'

...

repositories {
    ...
    
    mavenCentral()
    maven { url "https://jitpack.io" }
}

dependencies {
    ...
    
    compile('com.github.ltei.Kotlin_DataInterface:annotation:[version]')
    kapt('com.github.ltei.Kotlin_DataInterface:processor:[version]')
}
````