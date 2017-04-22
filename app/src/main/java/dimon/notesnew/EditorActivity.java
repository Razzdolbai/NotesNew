package dimon.notesnew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditorActivity extends AppCompatActivity {
    // Ключи по которым будем получать данные
    public final static String NameKey = "note_application_note_name";
    public final static String NoteDescriptionKey = "note_application_description";
    public final static String NoteIndex = "note_index"; // Индекс записи

    protected EditText EditorNoteName;    // Компонент реадктор назавания события
    protected EditText EditorDescription; //Компонент редактор наименования
    protected AlertDialog.Builder ad; // Диалоговое окно, состоящее из двух кнопок да - нет
    protected AlertDialog.Builder deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // Принимаем имя записки
        String NoteName = getIntent().getStringExtra(NameKey);
        // Принимаем описание записки
        String Description = getIntent().getStringExtra(NoteDescriptionKey);
        // Индекс записи в списке
        final String NotePositionInList = getIntent().getStringExtra(NoteIndex);

        // Теперь получаем экземпляры компонетов Редактор записки и описания
        EditorNoteName = (EditText) findViewById(R.id.EditorNoteNameText);
        EditorDescription = (EditText) findViewById(R.id.EditorNoteDescription);

        // Устанавливаем текст на компоненты.
        EditorNoteName.setText(NoteName);
        EditorDescription.setText(Description);

        String title = "Сохранение заметки";
        String message = "Хотите сохранить заметку?";
        String button1String = "Да";
        String button2String = "Нет";

        //Диалог сохранения объекта
        ad = new AlertDialog.Builder(this);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение

        // Обрабатываем нажатие клавиши Да.
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // Объеявляем интент для передачи сообщения главному активити
                Intent answerIntent = new Intent();
                // Получаем текст с наших компанентов, в которых содержится названия события и его описание
                String aNoteName = EditorNoteName.getText().toString();
                String aDescription = EditorDescription.getText().toString();

                // отправляем текст который мы написали в названии и описании
                answerIntent.putExtra(NameKey, aNoteName);
                answerIntent.putExtra(NoteDescriptionKey, aDescription);
                answerIntent.putExtra(NoteIndex, NotePositionInList);
                // указываем успешное завершение
                setResult(RESULT_FIRST_USER, answerIntent);
                //завершаем активность
                finish();
            }
        });

        // Обрабатываем нажатие клавиши Нет, обработчик автоматически вернет на форму
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent answerIntent = new Intent();
                // указываем успешное завершение
                setResult(RESULT_CANCELED, answerIntent);
            }
        });

        // Делам диалог отменяемым
        ad.setCancelable(true);
        // Добавляем обработчик события
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });

        //-----------------------------------------------------------------------

        String dtitle = "Удаление заметки";
        String dmessage = "Хотите удалить заметку?";
        String dbutton1String = "Да";
        String dbutton2String = "Нет";
        //Диалог удаления объекта

        deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(dtitle);  // заголовок
        deleteDialog.setMessage(dmessage); // сообщение

        // Обрабатываем нажатие клавиши Да.
        deleteDialog.setPositiveButton(dbutton1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // Объеявляем интент для передачи сообщения главному активити
                Intent answerIntent = new Intent();
                //Отправляем индекс который удаляем
                answerIntent.putExtra(NoteIndex, NotePositionInList);
                // указываем успешное завершение
                setResult(RESULT_FIRST_USER+1, answerIntent);
                //завершаем активность
                finish();
            }
        });

        // Обрабатываем нажатие клавиши Нет, обработчик автоматически вернет на форму
        deleteDialog.setNegativeButton(dbutton2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent answerIntent = new Intent();
                // указываем успешное завершение
                setResult(RESULT_CANCELED, answerIntent);
            }
        });

        // Делам диалог отменяемым
        deleteDialog.setCancelable(true);
        // Добавляем обработчик события
        deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    // Если нажата кнопка сохранить вызываем диалог
    public void onEditorSaveNoteButtonClick(View v) {
        ad.show();
    }
    // Если нажата кнопка удалить вызываем диалог
    public void onDeleteNodeButtonClick(View v) {
        deleteDialog.show();
    }
}
