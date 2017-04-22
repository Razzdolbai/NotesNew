package dimon.notesnew;

/**
 * Класс заметка у заметки есть название и есть описание
 * и два свойства для получения данных.
 */
public class SimpleNote {
    private String FName; // Название заметки
    private String FDescription; // Наменование заметки

    public SimpleNote(String name, String descrption){
        FName = name;
        FDescription = descrption;
    }

    public String getName(){
        return FName;
    }

    public String getDescription(){
        return FDescription;
    }
}