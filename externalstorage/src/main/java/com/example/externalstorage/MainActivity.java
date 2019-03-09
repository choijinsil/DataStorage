package com.example.externalstorage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

// AppCompatActivity 이전버젼과 호환성을 보장해주는 라이브러리를 제공해준다.
// Activity 상위(현재) 버젼으로 만든다. 이전 버젼과 호환성을 제공하지 않는다.

public class MainActivity extends AppCompatActivity {
    EditText et_text;
    TextView tv_result;
    Button btn_save, btn_load;

    final static String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_text = findViewById(R.id.et_text);
        tv_result = findViewById(R.id.tv_result);
        btn_save = findViewById(R.id.btn_save);
        btn_load = findViewById(R.id.btn_load);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 외부 저장소 권한 체크
                int check=checkExternalStorage();
                if(check!=PackageManager.PERMISSION_GRANTED){
                    return;
                }

                String data = et_text.getText().toString();
                try {
                    // private 앱안에서만 재생되게 하는 기술 - 멜론
                    // public 앱밖에서도 재생되게 하는 기술 - 스노우
                    // - 삭제시 남는다.
                    // File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                    // - 외부 저장소의 private영역 삭제시 같이 삭제된다.
                    File path = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), FILE_NAME);
                    File f = new File(path, FILE_NAME);

                    FileWriter writer = new FileWriter(f, false);  // 2번째 인자는 데이터 추가여부(append처럼)
                    PrintWriter out = new PrintWriter(writer);
                    out.println(data);
                    out.close();

                    // private 공간에 데이터 저장하기
                    File path_private = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), FILE_NAME);
                    FileWriter writer_private = new FileWriter(path_private, false);
                    PrintWriter out_private = new PrintWriter(writer_private);
                    out_private.println(data);
                    out_private.close();

                    tv_result.setText("데이터 저장 완료");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }); //  save종료!

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StringBuffer buffer = new StringBuffer();
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    File f = new File(path, FILE_NAME);    //path로 부터 file_name 을 읽어들여라.

                    BufferedReader reader = new BufferedReader(new FileReader(f));
                    String str = reader.readLine();
                    while (str != null) {
                        buffer.append(str + "\n");
                        str = reader.readLine();
                    }
                    tv_result.setText(buffer.toString());
                    reader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }   //oncreate종료
    // 마시멜로 이상에서 선택적 권한 부여
    int checkPermission(){

        // 권한 두개중 하나라도 없다면 true 반환
        boolean flag=
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED||
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED;

        if(flag){
            // 사용권한 없는경우
            // 인자 총 세개, this, 어떤권한들 부여할래?, 요청시 키값
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,9999);
            return PackageManager.PERMISSION_DENIED;
        }else{
            // 사용권한 있는경우
            return PackageManager.PERMISSION_GRANTED;
        }
    }
    //권한 체크용 메소드
    // 권한이 있다면 0, 없으면 -1을 리턴한다.
    int checkExternalStorage() {
        // checkSelfPermission: 앱 자체가 현재 권한을 체크해봐라.
        int read_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (write_permission == PackageManager.PERMISSION_GRANTED) {    // 0은 권한 있음
            // 읽기 쓰기 권한 모두 가능
            Toast.makeText(this,"읽기쓰기 가능",Toast.LENGTH_SHORT).show();
            return PackageManager.PERMISSION_GRANTED;
        } else if (write_permission == PackageManager.PERMISSION_DENIED && read_permission == PackageManager.PERMISSION_GRANTED) {
            // 읽기전용
            // 쓰기권한 없고 읽기 권한만 있는것
            Toast.makeText(this,"읽기전용",Toast.LENGTH_SHORT).show();
            return 99;
        } else {
            // 읽기 쓰기 권한 모두 안됨
            Toast.makeText(this,"읽기쓰기 불가능",Toast.LENGTH_SHORT).show();
            return PackageManager.PERMISSION_GRANTED;
        }

    }
}   //class종료
