package com.example.administrator.DataStorage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_text;
    Button btn_load, btn_save, btn_del, btn_cash, btn_cash_load;
    TextView tv_result;

    final static String FILE_NAME = "data.txt";   // 안드로이드 가상기기안의 특정공간에 만들어짐

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 객체화
        et_text = findViewById(R.id.et_text);
        btn_load = findViewById(R.id.btn_load);
        btn_save = findViewById(R.id.btn_save);
        tv_result = findViewById(R.id.tv_result);
        btn_del = findViewById(R.id.btn_del);
        btn_cash = findViewById(R.id.btn_cash);
        btn_cash_load = findViewById(R.id.btn_cash_load);

        btn_cash.setOnClickListener(this);
        btn_cash_load.setOnClickListener(this);
        btn_del.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        btn_save.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save: //데이터 저장
                String data = et_text.getText().toString();
                et_text.setText("");

                // java의 IO객체를 이용하여 파일을 저장한다.
                // io는 input과 output을 통칭한다.
                // 자바에서 파일을 내보내 저장하는 거기 때문에 out이다. 헷갈리지 말것
                // io객체 사용시 에러를 무조건 잡아줘야 한다. (io, db연결시 필수)
                // 외부상황은 자바에서 컨트롤 되는것이 아니다.

                try {
                    //FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);   //단일
                    FileOutputStream fos = openFileOutput(FILE_NAME, MODE_APPEND);  //추가
                    PrintWriter writer = new PrintWriter(fos);
                    writer.print(data);
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_load: //데이터 텍스트뷰로 불러오기
                StringBuffer buffer = new StringBuffer();
                try {

                    FileInputStream fis = openFileInput(FILE_NAME);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String str = reader.readLine();   //한줄씩 읽어들여라
                    while (str != null) {  // 마지막 라인까지 반복해라
                        buffer.append(str + "\n");
                        str = reader.readLine();
                    }
                    tv_result.setText(buffer.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_del:
                //내부저장소 files폴더의 FILE_NAME을 삭제
                File file = new File(getFilesDir(), FILE_NAME);
                file.delete();
                break;

            case R.id.btn_cash:
                String data_cashe = et_text.getText().toString();
                et_text.setText("");
                try {
                    File casheDir = getCacheDir();
                    File casheFile = new File(casheDir.getAbsolutePath(), FILE_NAME);
                    FileOutputStream fos = new FileOutputStream(casheFile);  //추가
                    PrintWriter writer = new PrintWriter(fos);
                    writer.print(data_cashe);
                    writer.close();
                } catch (Exception e) {

                }
                break;
            case R.id.btn_cash_load:
                // 캐시는 임시저장영역이라 무조건 새로 만들어 진다. append가 없다. 즉, 수정이 없다.
                StringBuffer buffer_cahe = new StringBuffer();
                try {
                    FileInputStream fis = new FileInputStream(new File(getCacheDir(), FILE_NAME));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String str = reader.readLine();   //한줄씩 읽어들여라
                    while (str != null) {  // 마지막 라인까지 반복해라
                        buffer_cahe.append(str + "\n");
                        str = reader.readLine();
                    }
                    tv_result.setText(buffer_cahe.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
