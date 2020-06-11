package com.gigafort.todolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText txtTarefa;
    private Button btnAdicionar;
    private ListView listaTarefas;

    private SQLiteDatabase database;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       try {
            txtTarefa = findViewById(R.id.tarefaId);
            btnAdicionar = findViewById(R.id.btnAdcId);
            listaTarefas = findViewById(R.id.listaId);

            //Banco de dados
            database = openOrCreateDatabase("apptarefas2", MODE_PRIVATE, null);

            //Criar Tabela
            database.execSQL("CREATE TABLE IF NOT EXISTS tbTarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            btnAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String txtDigitado = txtTarefa.getText().toString();
                    SalvarTarefa(txtDigitado);
                    }
            });

           listaTarefas.setLongClickable(true);
           listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
               @Override
               public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                   RemoverTarefa( ids.get( position ) );
                   return true;
               }
           });

            /*listaTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Log.i("ITEM: ", position + "/" + ids.get(position));
                    RemoverTarefa(ids.get(position));
                }
            });*/

            RecuperarTarefas();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void SalvarTarefa(String texto){

        try{

            if(!texto.equals("")){
                database.execSQL("INSERT INTO tbTarefas(tarefa) VALUES ('" + texto + "') ");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show();
                RecuperarTarefas();
                txtTarefa.setText("");
            }
            else{
                Toast.makeText(MainActivity.this, "Digite uma tarefa", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void RecuperarTarefas(){
        try{
            Cursor cursor = database.rawQuery("SELECT * FROM tbTarefas ORDER BY id DESC", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            //Criar adaptador
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                                        android.R.layout.simple_list_item_2,
                                        android.R.id.text2,
                                        itens
            );

            listaTarefas.setAdapter(itensAdaptador);

            cursor.moveToFirst();
            while (cursor != null){

                Log.i("Resultado - ", "Tarefa: " + cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt( cursor.getString(indiceColunaId) ));
                cursor.moveToNext();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void RemoverTarefa(Integer id){

        try{
            database.execSQL("DELETE FROM tbTarefas WHERE id = " + id);
            RecuperarTarefas();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
