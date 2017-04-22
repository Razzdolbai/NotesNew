package dimon.notesnew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

public class AddNoteActivity extends AppCompatActivity {

    protected EditText NoteName;    // Компонент реадктор назавания события
    protected EditText Description; //Компонент редактор наименования

    // Переменные необходимые для передачи данных по ключу, для главной формы.
    public final static String NameKey = "note_application_note_name";
    public final static String NoteDescriptionKey = "note_application_description";

    protected AlertDialog.Builder ad; //Диалоговое окно, состоящее из двух кнопок да - нет

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        // Получаем объекты компоненты по имени, получаем их после вызова нашего окна.
        NoteName = (EditText) findViewById(R.id.NoteNameText);
        Description = (EditText) findViewById(R.id.NoteDescription);
//        NoteName.setText("<Новая заметка>");
//        Description.setText("Введите описание заметки");

        String title = "Сохранение заметки";
        String message = "Хотите сохранить заметку?";
        String button1String = "Да";
        String button2String = "Нет";

        ad = new AlertDialog.Builder(this);
        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение

        // Обрабатываем нажатие клавиши Да.
        ad.setPositiveButton(button1String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                // Объеявляем интент для передачи сообщения главному активити
                Intent answerIntent = new Intent();
                // Получаем текст с наших компонентов, в которых содержится названия события и его описание
                String aNoteName = NoteName.getText().toString();
                String aDescription = Description.getText().toString();

                // отправляем текст который мы написали в названии и описании
                answerIntent.putExtra(NameKey, aNoteName);
                answerIntent.putExtra(NoteDescriptionKey, aDescription);
                // указываем успешное завершение
                setResult(RESULT_OK, answerIntent);
                //завершаем активность
                finish();
            }
        });

        // Обрабатываем нажатие клавиши Нет, обработчик автоматически вернет на форму
        ad.setNegativeButton(button2String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent answerIntent = new Intent();
                // указываем успешное завершение
                setResult(RESULT_CANCELED, answerIntent);
            }
        });

        // Делам диалог отменяемым
        ad.setCancelable(true);
        // Добавлдяем обработчик события\
        ad.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
    }

    public void onSaveButtonClick(View v) {
        ad.show();
    }
}
