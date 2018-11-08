package boboteca.Comandos;

import boboteca.DAO.BookDAO;
import boboteca.Model.Book;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.prism.impl.Disposer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

import static boboteca.Comandos.OpenConfirmModal.openConfirmPanel;

public class LoadTableViewBooks {

    public void loadTableViewBooks(String search, String filter, TableColumn bookId, TableColumn bookName, TableColumn bookAuthor, TableColumn bookCategory, TableColumn bookYear, TableColumn bookDisponibility, List<Book> bookList, JFXToggleButton availableOnly, TableView bookTableView, TableColumn bookRemove) {
        bookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookName.setCellValueFactory(new PropertyValueFactory<>("name"));
        bookAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        bookYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        bookDisponibility.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Book, String>, ObservableValue<String>>) param -> {
            final Book book = param.getValue();
            return new SimpleObjectProperty<>((book.getDisponibility())?"Sim":"Não");
        });

        String[] parts = search.toUpperCase().split(" ");

        ObservableList<Book> bookObservableList = FXCollections.observableArrayList();
        for (Book b: bookList) {
            if(b.getDisponibility() && availableOnly.isSelected()){
                new LoadTableViewBooks().mountBookList(filter, parts, bookObservableList, b);
            }else if (!availableOnly.isSelected()){
                new LoadTableViewBooks().mountBookList(filter, parts, bookObservableList, b);
            }
        }

        if(bookRemove != null){
            bookRemove.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Disposer.Record, Boolean>, ObservableValue<Boolean>>) p -> new SimpleBooleanProperty(p.getValue() != null));

            bookRemove.setCellFactory(
                    (Callback<TableColumn<Disposer.Record, Boolean>, TableCell<Disposer.Record, Boolean>>) p -> new ButtonCell(search,filter,bookId,bookName,bookAuthor,bookCategory,bookYear,bookDisponibility,bookList,availableOnly,bookTableView,bookRemove));
        }

        bookTableView.setItems(bookObservableList);
    }

    private void mountBookList(String filter, String[] parts, ObservableList<Book> bookObservableList, Book b) {
        boolean match = true;
        String entryText = "";
        switch (filter){
            case "name":
                entryText = b.getName();
                break;
            case "author":
                entryText = b.getAuthor();
                break;
            case "category":
                entryText = b.getCategory();
                break;
            default:
                break;
        }
        for (String part: parts) {
            if (!entryText.toUpperCase().contains(part)) {
                match = false;
                break;
            }
        }

        if (match) {
            bookObservableList.add(b);
        }
    }

    public class ButtonCell extends TableCell<Disposer.Record, Boolean> {
        final JFXButton cellButton = new JFXButton("X");

        public ButtonCell(String search, String filter, TableColumn bookId, TableColumn bookName, TableColumn bookAuthor, TableColumn bookCategory, TableColumn bookYear, TableColumn bookDisponibility, List<Book> bookList, JFXToggleButton availableOnly, TableView bookTableView, TableColumn bookRemove){
            cellButton.getStyleClass().addAll("btn-danger");
            cellButton.setOnAction(t -> {
                Book currentBook = (Book) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                try {
                    if(openConfirmPanel("Deseja realmente exlcuir o livro "+currentBook.getName()+"?")){
                        new BookDAO().removeBook(currentBook.getId());
                        bookList.remove(currentBook);
                        loadTableViewBooks("","",bookId,bookName,bookAuthor,bookCategory,bookYear,bookDisponibility,bookList,availableOnly,bookTableView,bookRemove);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }

}
