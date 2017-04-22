package dimon.notesnew;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static android.app.Activity.RESULT_OK;

public class MainNoteForm extends AppCompatActivity {
    // Список
    private ListView mListView; //ListView
    // Список наших событии
    private ArrayList<SimpleNote> mNotesList = new ArrayList<>();// Список заметок.

    private MyArrayAdapter mArrayAdapter;
    // Ключи для хранения переданных данных, в форму(Активность) добавления
    public final static String NameKey = "note_application_note_name";
    public final static String NoteDescriptionKey = "note_application_description";
    public final static String NoteIndex = "note_index"; // Индекс записи
    public final String MySavedNotesFile = "saved_notes"; // Наименование файла с сохраненными событиями

    // Переменная которая передается второй активности
    // если она 0, то не сохраняем если 1 то схраняем.
    static final private Integer SaveNoteFlag  = 0;
    // лог
    final String LOG_TAG = "myLogs";

    //  Загрузка меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.getItem(0).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }
    // Обработка нажатии на меню
    // Перебор списка и удаление элементов

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Вызов кнопки удалить на меню
            case R.id.action_notification1:
                mArrayAdapter.DeleteSectedElements();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_note_form);

        mListView = (ListView) findViewById(R.id.listView);

        // Инициализируем список заметок из сохранения.
        initList();

        mArrayAdapter = new MyArrayAdapter(this, android.R.layout.select_dialog_multichoice, android.R.id.text1, mNotesList);

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Устанваливаем фокус ввода убираем, чтобы небыло постоянно выделенных элементов
        //mListView.setFocusable(false);

        // Устанавливаем адаптер, который отвечает за построения списка
        mListView.setAdapter(mArrayAdapter);

        // Обработчик события нажатии на списке элементов, при нажатии на списке вызываем форму.
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Проверка на то, что не вышли за пределы списка.
                // Может возникнуть ситуация при котором удаленные элементы не успели
                //обновить интерфейс и могут быть выделены. Это вызывет ошибку,поэтому защитимся
                if (position < mNotesList.size() && position >= 0) {

                    // создаем intent (Намерение открыть форму Редактора заметки)
                    Intent intent = new Intent(MainNoteForm.this, EditorActivity.class);

                    SimpleNote aSelectedNote = mNotesList.get(position); // Получаем текущий выделеный элемент.

                    // Передаем открываемой форме имя записки и описание
                    intent.putExtra(NameKey, aSelectedNote.getName());
                    intent.putExtra(NoteDescriptionKey, aSelectedNote.getDescription());
                    // Передаем индекс записи
                    intent.putExtra(NoteIndex, String.valueOf(position));
                    // Открываем форму или по другому активность
                    startActivityForResult(intent, SaveNoteFlag);
                }
            }
        });
    }

    // Обработка нажатия клавиши новая заметка.
    public void onClick(View v) {

        // Надо вызывать другое окно называемое activity- добавление новой заметки
        //Содаем интент который находит наш класс редактирования
        Intent intent = new Intent(MainNoteForm.this, AddNoteActivity.class);
        // вызываем данную активити.
        // Создаем два ключа по которому заберем значения с формы редактирования сообщения

        EditText noteName = (EditText) findViewById(R.id.NoteNameText);
        EditText noteDescription=(EditText) findViewById(R.id.NoteDescription);
        // Запускаем вторую форму и ждем пока от нее придет какой либо результат
        startActivityForResult(intent, SaveNoteFlag);
    }
    // Загрузка списка. Здесь загружаем весь список заметок.
    private void initList() {
        //   Код загрузки списка из сохраненной базы
        Context aContext = getApplicationContext();

        File file = new File(aContext.getFilesDir(), MySavedNotesFile);
        Scanner aFileScanner = null;
        try {
            aFileScanner = new Scanner(file);
            while (aFileScanner.hasNext())
            {
                String aNoteName = aFileScanner.nextLine();
                if (aFileScanner.hasNext()) {
                    String aNoteDescription = aFileScanner.nextLine();
                    mNotesList.add(new SimpleNote(aNoteName, aNoteDescription));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        if (mNotesList.size() ==0){
//            SimpleNote aNote= new SimpleNote("новая заметка","описание");
//            mNotesList.add(aNote);
//            mNotesList.add(aNote);
//            mNotesList.add(aNote);
//        }
    }

    // Событие при закрытии формы
    protected void onDestroy() {
        // Перебираем наши список и сохраняем в файл.
        Context  aContext = getApplicationContext();
        File file = new File(aContext.getFilesDir(), MySavedNotesFile);
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));

            for (int i = 0; i < mNotesList.size(); i++) {
                writer.println(mNotesList.get(i).getName());
                writer.println(mNotesList.get(i).getDescription());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    // Обрабатываем ответ который придет от формы по добавлению нового события.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Здесь должны получить текст от формы вытащить их по ключу, и добавить новое события.
        if (resultCode == RESULT_OK ) {
            String NoteName = data.getStringExtra(NameKey);
            String NodeDescription = data.getStringExtra(NoteDescriptionKey);
            // создаем новую заметку
            SimpleNote aNewNote = new SimpleNote(NoteName, NodeDescription);
            // добавлеяем в список.
            mNotesList.add(aNewNote);
            // Говорим адаптеру, что количество данных изменилось
            // адптер автоматически перестроит список listview
            // Вызываем сообщение что добавили новую заметку
            Toast toast = Toast.makeText(getApplicationContext(), "Добавлена новая заметка", Toast.LENGTH_SHORT);
            toast.show();
        }

        if(resultCode == RESULT_CANCELED){
            Toast toast = Toast.makeText(getApplicationContext(), "Добавление отменено", Toast.LENGTH_SHORT);
            toast.show();
        }

        // Если событие на изменение, то находим в списке событие и изменяем его
        if (resultCode == RESULT_FIRST_USER)
        {
            String NoteName = data.getStringExtra(NameKey);
            String NodeDescription = data.getStringExtra(NoteDescriptionKey);
            String Index = data.getStringExtra(NoteIndex);
            //Получаем индекс.
            int IntIndex = Integer.parseInt(Index.toString());
            // Создаем новый экземляр
            SimpleNote aEditNote = new SimpleNote(NoteName, NodeDescription);
            // Заменяем существующий
            mNotesList.set(IntIndex, aEditNote);
        }

        if (resultCode == RESULT_FIRST_USER+1)
        {
            // Получаем индекс
            String Index = data.getStringExtra(NoteIndex);
            int IntIndex = Integer.parseInt(Index.toString());
            // Удаляем элемент по индексу.
            mNotesList.remove(IntIndex);
        }
        // Обновляем список
        mArrayAdapter.UpdateList(mNotesList);
    }

    private class MyArrayAdapter extends ArrayAdapter<SimpleNote> {

        private HashMap<Integer, Boolean> mCheckedMap = new HashMap<>();

        public MyArrayAdapter(Context context, int resource,
                              int textViewResourceId, List<SimpleNote> objects) {
            super(context, resource, textViewResourceId, objects);

            for (int i = 0; i < objects.size(); i++) {
                mCheckedMap.put(i, false);
            }
        }

        public void DeleteSectedElements(){

            List<Integer> aIndexes = getCheckedItemPositions();
            for(int i=0; i< aIndexes.size();i++){
                SimpleNote aNote = mNotesList.get(aIndexes.get(i));
                mArrayAdapter.remove(aNote);
            }
            mCheckedMap.clear();
            mArrayAdapter.UpdateList(mNotesList);
        }

        public void UpdateList(List<SimpleNote> objects){
            mCheckedMap.clear();
            for (int i = 0; i < objects.size(); i++) {
                mCheckedMap.put(i, false);
            }
            notifyDataSetChanged();
        }

        public void toggleChecked(int position) {
            if (mCheckedMap.get(position)) {
                mCheckedMap.put(position, false);
            } else {
                mCheckedMap.put(position, true);
            }
            notifyDataSetChanged();
        }

        public List<Integer> getCheckedItemPositions() {
            List<Integer> checkedItemPositions = new ArrayList<>();

            for (int i = 0; i < mCheckedMap.size(); i++) {
                if (mCheckedMap.get(i)) {
                    (checkedItemPositions).add(i);
                }
            }
            return checkedItemPositions;
        }

        public List<String> getCheckedItems() {
            List<String> checkedItems = new ArrayList<>();

            for (int i = 0; i < mCheckedMap.size(); i++) {
                if (mCheckedMap.get(i)) {
                    (checkedItems).add(mNotesList.get(i).getName());
                }
            }
            return checkedItems;
        }

        public boolean isChecked(int position) {
            return mCheckedMap.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.list_item, parent, false);
            }

            TextView textView = (TextView) row .findViewById(R.id.item_textView);
            final CheckBox checkBox = (CheckBox) row.findViewById(R.id.item_checkBox);

            textView.setText(mNotesList.get(position).getName());

            // Отмечаем выбранные элементы в нашем параллельном списке отмеченных чек-боксов
            checkBox.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Boolean checked = mCheckedMap.get(position);
                    if (checked != null) {
                        mCheckedMap.put(position, true);
                    }
                    if (checked) {
                        mCheckedMap.put(position, false);
                    }
                }
            });
            return row;
        }
    }
}
