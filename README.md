### DataInterface

An annotation to generate different kotlin classes representing a model

this...

````kotlin
@DataInterface
interface IModel {
    val id: Int
    val username: String
    val phoneNumber: String?
    val emails: List<String>
    val properties: Map<String, String>
}
````

...will generate this :

````kotlin
interface Model : IModel {
  fun toMutable(): MutableModel = MutableModel(id, username, phoneNumber, emails.toMutableList(),
      properties.toMutableMap())

  fun toUnsafe(): UnsafeModel = UnsafeModel(id, username, phoneNumber, emails.toMutableList(),
      properties.toMutableMap())

  companion object {
    fun new(
      id: Int,
      username: String,
      phoneNumber: String?,
      emails: List<String>,
      properties: Map<String, String>
    ): Model = ModelImpl(id, username, phoneNumber, emails, properties)

    fun newMutable(
      id: Int,
      username: String,
      phoneNumber: String?,
      emails: MutableList<String>,
      properties: MutableMap<String, String>
    ): MutableModel = MutableModel(id, username, phoneNumber, emails, properties)

    fun newUnsafe(
      id: Int? = null,
      username: String? = null,
      phoneNumber: String? = null,
      emails: MutableList<String>? = null,
      properties: MutableMap<String, String>? = null
    ): UnsafeModel = UnsafeModel(id, username, phoneNumber, emails, properties)
  }
}

private class ModelImpl(
  override val id: Int,
  override val username: String,
  override val phoneNumber: String?,
  override val emails: List<String>,
  override val properties: Map<String, String>
) : Model

class MutableModel(
  override var id: Int,
  override var username: String,
  override var phoneNumber: String?,
  override var emails: MutableList<String>,
  override var properties: MutableMap<String, String>
) : Model {
  override fun toUnsafe(): UnsafeModel = UnsafeModel(id, username, phoneNumber, emails, properties)
}

class UnsafeModel(
  var id: Int? = null,
  var username: String? = null,
  var phoneNumber: String? = null,
  var emails: MutableList<String>? = null,
  var properties: MutableMap<String, String>? = null
) {
  fun toSafeOrNull(): MutableModel? {
    val id = this.id
    val username = this.username
    val phoneNumber = this.phoneNumber
    val emails = this.emails
    val properties = this.properties

    return if (id != null && username != null && phoneNumber != null && emails != null && properties
        != null) {
       MutableModel(id, username, phoneNumber, emails, properties)
    } else null
  }
}
````

### How to use

In your build.gradle :

````groovy
apply plugin: 'kotlin-kapt'

...

dependencies {
    ...
    
    compile project(':annotation')
    kapt project(':processor')
    
    ..
}
````

That's all