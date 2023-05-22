module hw6_javafx_db
{
  requires javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires java.sql;

  opens phonebook;
  opens phonebook.controllers;
}