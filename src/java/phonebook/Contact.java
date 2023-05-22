package phonebook;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;

public class Contact implements Serializable {

  // Constructors,
  public Contact(String id, String firstName, String lastName, String patronymic, String mobilePhone,
      String homePhone, String address, String dateOfBirth, String comment) {
    setId(id);
    setFirstName(firstName);
    setLastName(lastName);
    setPatronymic(patronymic);
    setMobilePhone(mobilePhone);
    setHomePhone(homePhone);
    setAddress(address);
    setDateOfBirth(dateOfBirth);
    setComment(comment);
  }

  public Contact() {
    this("", "", "", "", "", "", "", "", "");
  }

  // Fields.

  private final SimpleStringProperty id = new SimpleStringProperty("");
  private final SimpleStringProperty firstName = new SimpleStringProperty("");
  private final SimpleStringProperty lastName = new SimpleStringProperty("");
  private final SimpleStringProperty patronymic = new SimpleStringProperty("");
  private final SimpleStringProperty mobilePhone = new SimpleStringProperty("");
  private final SimpleStringProperty homePhone = new SimpleStringProperty("");
  private final SimpleStringProperty phone = new SimpleStringProperty("");
  private final SimpleStringProperty address = new SimpleStringProperty("");
  private final SimpleStringProperty dateOfBirth = new SimpleStringProperty("");
  private final SimpleStringProperty comment = new SimpleStringProperty("");

  // Don't trust IDEA! Don't delete these 'unused' methods!!!
  // Or else the table won't update properly =(
  // Getters and setters.

  public SimpleStringProperty firstNameProperty() {
    return firstName;
  }
  public SimpleStringProperty lastNameProperty() {
    return lastName;
  }
  public SimpleStringProperty patronymicProperty() {
    return patronymic;
  }
  public SimpleStringProperty mobilePhoneProperty() {
    return mobilePhone;
  }
  public SimpleStringProperty homePhoneProperty() {
    return homePhone;
  }
  public SimpleStringProperty phoneProperty() {
    return phone;
  }
  public SimpleStringProperty addressProperty() {
    return address;
  }
  public SimpleStringProperty dateOfBirthProperty() {
    return dateOfBirth;
  }
  public SimpleStringProperty commentProperty() {
    return comment;
  }

  public String getFirstName() {
    return firstName.get();
  }

  public void setFirstName(String firstName) {
    this.firstName.set(firstName);
  }

  public String getLastName() {
    return lastName.get();
  }

  public void setLastName(String lastName) {
    this.lastName.set(lastName);
  }

  public String getPatronymic() {
    return patronymic.get();
  }

  public void setPatronymic(String patronymic) {
    this.patronymic.set(patronymic);
  }

  public String getMobilePhone() {
    return mobilePhone.get();
  }

  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone.set(mobilePhone);
    setPhone(this.mobilePhone.get(), this.homePhone.get());
  }

  public String getHomePhone() {
    return homePhone.get();
  }

  public void setHomePhone(String homePhone) {
    this.homePhone.set(homePhone);
    setPhone(this.mobilePhone.get(), this.homePhone.get());
  }

  private void setPhone(String mobilePhone, String homePhone) {
    // This setter is called every time mobile or home phone number changes.
    if (mobilePhone.isEmpty() || mobilePhone.isBlank()) {
      this.phone.set(homePhone);
    } else if (homePhone.isEmpty() || homePhone.isBlank()) {
      this.phone.set(mobilePhone);
    } else {
      this.phone.set(mobilePhone + "/" + homePhone);
    }
  }

  public String getAddress() {
    return address.get();
  }

  public void setAddress(String address) {
    this.address.set(address);
  }

  public String getDateOfBirth() {
    return dateOfBirth.get();
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth.set(dateOfBirth);
  }

  public String getComment() {
    return comment.get();
  }

  public void setComment(String comment) {
    this.comment.set(comment);
  }

  public String getId() {
    return id.get();
  }

  public SimpleStringProperty idProperty() {
    return id;
  }

  public void setId(String id) {
    this.id.set(id);
  }
}
