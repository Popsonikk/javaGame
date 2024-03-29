package database;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.DeleteButtonCell;
import sample.MainWordDBController;
import sample.ScoreWindowController;
import sample.TableViewClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SQLCommands {
    public static ObservableList<ObservableList<String>> getWordList()
    {
        ObservableList<ObservableList<String>> list= FXCollections.observableArrayList();
        String command = "SELECT word, category FROM wordsTable";
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
            Statement statement= conn.createStatement()) {
            ResultSet resultSet=statement.executeQuery(command);
            while (resultSet.next())
            {
                ObservableList<String> row= FXCollections.observableArrayList();
                row.add(resultSet.getString("word"));
                row.add(resultSet.getString("category"));
                list.add(row);
            }

        }
        catch (SQLException e) {
            System.out.println("Wystąpił błąd podczas pobierania listy: " + e.getMessage());
        }
        return list;
    }
    public static ObservableList<ObservableList<String>> getBasicScoreList()
    {
        ObservableList<ObservableList<String>> list= FXCollections.observableArrayList();
        String command = "SELECT nick,score, frames FROM scoreTable";
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
            Statement statement= conn.createStatement()) {
            ResultSet resultSet=statement.executeQuery(command);
            while (resultSet.next())
            {
                ObservableList<String> row= FXCollections.observableArrayList();
                row.add(resultSet.getString("nick"));
                row.add(resultSet.getString("score"));
                row.add(resultSet.getString("frames"));
                list.add(row);
            }

        }
        catch (SQLException e) {
            System.out.println("Wystąpił błąd podczas pobierania listy: " + e.getMessage());
        }
        return list;
    }
    public static void insertWord(String wordToAdd,String categoryToAdd, MainWordDBController mw)
    {
        String command = "INSERT INTO wordsTable (word,category) VALUES (?,?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
             PreparedStatement statement = conn.prepareStatement(command)) {
            statement.setString(1,wordToAdd);
            statement.setString(2,categoryToAdd);
            statement.executeUpdate();
            int size= mw.getDataSize();
            ObservableList<String >row=FXCollections.observableArrayList(wordToAdd,categoryToAdd);
            mw.setData(row);
            if(size==0)
            {
                TableColumn<ObservableList<String>,Void> deleteColumn=new TableColumn<>("Delete");
                deleteColumn.setCellFactory(DeleteButtonCell.forTableColumn());
                TableView<ObservableList<String>> tableView=mw.getTableView();
                tableView.getColumns().remove(2);
                deleteColumn.setPrefWidth(200);
                tableView.getColumns().add(deleteColumn);
                mw.setTableView(tableView);
            }
            System.out.println("Słowo zostało dodane.");

        } catch (SQLException e) {
            System.out.println("Wystąpił błąd podczas dodawania wyrazu do tabeli: " + e.getMessage());
        }
    }
    public static int getTableSize(String name)
    {
        int res=0;
        String command= "SELECT COUNT(*) AS rowCount FROM "+name;
        try(Connection conn = DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
            Statement statement= conn.createStatement()) {
            ResultSet resultSet=statement.executeQuery(command);
            //sprawdzenie czy wynik zwrócił jakikolwiek wynik (ustawienie na pierwszą kolumnę wyniku)
            if (resultSet.next())
                res= resultSet.getInt("rowCount");
        }
        catch (SQLException e) {
            System.out.println("Wystąpił błąd podczas wykonania polecenia: " + e.getMessage());
        }
        return res;
    }
    public static void deleteWord(String wordToDelete)
    {
        String command= "Delete FROM wordsTable WHERE word= (?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
             PreparedStatement statement = conn.prepareStatement(command)) {
            statement.setString(1,wordToDelete);
            int changedRecords= statement.executeUpdate();
            if(changedRecords>0)
                System.out.println("Słowo zostało usunięte.");
            else
                System.out.println("Nie znaleziono wyrazu");

        } catch (SQLException e) {
            System.out.println("Wystąpił błąd podczas usuwania wyrazu z tabeli: " + e.getMessage());
        }
    }
    public static void clearTable(String name)
    {
        try(Connection conn=DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
        Statement statement= conn.createStatement())
        {
            statement.executeUpdate("DELETE FROM "+name);
        }
        catch (SQLException e)
        {
            System.out.println("Wystąpił błąd podczas czyszczenia tabeli "+name+" : "+e);
        }
    }
    public static void addAccount(String nick, String password)
    {
        try(Connection conn=DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
            PreparedStatement statement= conn.prepareStatement("INSERT INTO accountTable (nick,password) VALUES ( ?,?)"))
        {
            statement.setString(1,nick);
            statement.setString(2, Base64.getEncoder().encodeToString(password.getBytes()));
            statement.executeUpdate();
            System.out.println("Dane wstawiono poprawnie");
        }
        catch (SQLException e)
        {
            System.out.println("Wystąpił błąd podczas wstawiania konta:"  +e);
        }
    }
    public static String getAccount(String nick,String password)
    {
        try(Connection conn=DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
            PreparedStatement statement= conn.prepareStatement("SELECT * FROM accountTable WHERE nick=(?) AND password=(?)"))
        {
            statement.setString(1,nick);
            statement.setString(2, Base64.getEncoder().encodeToString(password.getBytes()));
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next())
                return resultSet.getString("nick");
            else
                return "";

        }
        catch (SQLException e)
        {
            System.out.println("Wystąpił błąd podczas wstawiania konta:"  +e);
            return "";
        }
    }
    public static void setScore(String nick, int score, int frames, TableViewClass sw)
    {
        try(Connection conn=DriverManager.getConnection("jdbc:sqlite:HangMan/src/main/resources/myDatabase.db");
            PreparedStatement statement= conn.prepareStatement("INSERT INTO scoreTable (nick,score,frames) VALUES ( ?,?,?)"))
        {
            statement.setString(1,nick);
            statement.setInt(2, score);
            statement.setInt(3,frames);
            statement.executeUpdate();
            ObservableList<String> row=FXCollections.observableArrayList();
            row.add(nick);
            row.add(Integer.toString(score));
            row.add(Integer.toString(frames));
            sw.setData(row);
            System.out.println("Dane wstawiono poprawnie");
        }
        catch (SQLException e)
        {
            System.out.println("Wystąpił błąd podczas wstawiania konta:"  +e);
        }
    }
}
