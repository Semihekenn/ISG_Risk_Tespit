package com.dreamsoft.isgrisktespit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    Button btnCallendar,btnMain,btnSelect;
    EditText edUygunsuz,edEtkilenen,edAlinmisOnlem,edAciklamaTehlike,edAciklamaRisk,edAlinmasiGerekenOnlem,edUygunsuzlugunGiderilmesi;
    Spinner spOlasilik,spFrekans,spSiddet;
    CheckBox chDuzeltici,chOnleyici,chGiderildi;
    public final static int KEY_PERMISSION=123;
    Context context=this;
    Bitmap bitmap, scaledBitmap,scaledBitmap2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){

        }
        if (requestCode==15){
            Uri uriImg=data.getData();
            btnSelect.setText(uriImg.toString());
            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uriImg, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
               scaledBitmap=Bitmap.createScaledBitmap(bitmap,100,100,true);
                scaledBitmap2=bitmap.createBitmap(bitmap,0,0,100,100);


                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void Init(){
        btnCallendar=findViewById(R.id.btn_calendar);
        btnMain=findViewById(R.id.btn_main);
        btnSelect=findViewById(R.id.btn_select_img);
        edUygunsuz=findViewById(R.id.ed_uygunsuz);
        edEtkilenen=findViewById(R.id.ed_etkilenen);
        edAlinmisOnlem=findViewById(R.id.ed_alınmıs_onlem);
        edAciklamaTehlike=findViewById(R.id.ed_aciklama_tehlike);
        edAciklamaRisk=findViewById(R.id.ed_aciklama_risk);
        edAlinmasiGerekenOnlem=findViewById(R.id.ed_alinmasigereken_onlem);
        edUygunsuzlugunGiderilmesi=findViewById(R.id.ed_uygunsuzlugun_giderilmesi);
        spOlasilik=findViewById(R.id.sp_olasilik);
        spFrekans=findViewById(R.id.sp_frekans);
        spSiddet=findViewById(R.id.sp_siddet);
        chDuzeltici=findViewById(R.id.ch_duzeltici);
        chOnleyici=findViewById(R.id.ch_onleyici);
        chGiderildi=findViewById(R.id.ch_giderildi);

        ArrayAdapter adapter=ArrayAdapter.createFromResource(getApplicationContext(),R.array.list_olasilik, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOlasilik.setAdapter(adapter);
        adapter=ArrayAdapter.createFromResource(getApplicationContext(),R.array.list_frekans, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrekans.setAdapter(adapter);
        adapter=ArrayAdapter.createFromResource(getApplicationContext(),R.array.list_siddet, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSiddet.setAdapter(adapter);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,15);
            }
        });

        btnCallendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int gun, ay, yil;
                    Calendar thisTime= Calendar.getInstance();
                    gun=thisTime.get(Calendar.DAY_OF_MONTH);
                    ay=thisTime.get(Calendar.MONTH);
                    yil=thisTime.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog= new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnCallendar.setText(dayOfMonth +"."+(month+1)+"."+year);
                    }
                },yil,ay,gun);

                datePickerDialog.setTitle("Tespit Tarihi");
                datePickerDialog.show();


                 }
        });


        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlPermission();
            }
        });
    }

    @AfterPermissionGranted(KEY_PERMISSION)
    private void controlPermission(){
        String[] perms= {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getApplicationContext(),perms)){
            makePDF();
        }else{
            EasyPermissions.requestPermissions(this ,"İşleminizi gerçekleştirebilmek için uygulama izinlerine onay vermelisiniz",KEY_PERMISSION,perms);
        }
    }

    private void makePDF(){
        PdfDocument document=new PdfDocument();
        Paint paint=new Paint();

        PdfDocument.PageInfo myPageInfo=new PdfDocument.PageInfo.Builder(250,400,1).create();
        PdfDocument.Page myPage=document.startPage(myPageInfo);

        Canvas canvas=myPage.getCanvas();
        Rect textBounds = new Rect();

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(10);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("İSG RİSK TESPİT VE DÖF FORMU",myPageInfo.getPageWidth()/2,8,paint);
        paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(1,20,(myPageInfo.getPageWidth())-1,(myPageInfo.getPageHeight()/2)-50,paint);
        canvas.drawLine(myPageInfo.getPageWidth()/5,20,myPageInfo.getPageWidth()/5,44,paint);
        canvas.drawLine(7*myPageInfo.getPageWidth()/10,20,7*myPageInfo.getPageWidth()/10,44,paint);

        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);



        String[] blockKısım={"Tespit Tarihi","Uygunsuz lukYeri","Etkilenen Kişi/Grup","Alınmış Önlem"};
        String[] blockCevap={btnCallendar.getText().toString(),edUygunsuz.getText().toString(),edEtkilenen.getText().toString(),edAlinmisOnlem.getText().toString()};
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(3f);
        //paint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));

        int startX=2;
        int startY=22;
        int endX=7*myPageInfo.getPageWidth()/10;
        for (int i=0;i<4;i++){

           // paint.setColor(ResourcesCompat.getColor(getResources(), R.color.gray, null));

            canvas.drawText(blockKısım[i],startX,startY+3,paint);
            //paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            canvas.drawText(blockCevap[i],(myPageInfo.getPageWidth()/5)+2,startY+3,paint);
            //paint.setColor(ResourcesCompat.getColor(getResources(), R.color.black, null));
            canvas.drawLine(startX,startY+4,endX,startY+4,paint);
            startY+=5;
        }

        canvas.drawText("Risk Derecesi",(myPageInfo.getPageWidth()-(2*myPageInfo.getPageWidth()/10)),25,paint);
        canvas.drawLine(7*myPageInfo.getPageWidth()/10,26,myPageInfo.getPageWidth()-1,26,paint);


        String[] strRısk={"Olasılık","Frekans","Şiddet","Risk"};
        String[] strRıskUserr={spOlasilik.getSelectedItem().toString(),spFrekans.getSelectedItem().toString(),spSiddet.getSelectedItem().toString(),
                (Double.parseDouble(spFrekans.getSelectedItem().toString())*Double.parseDouble(spOlasilik.getSelectedItem().toString())
                        *Double.parseDouble(spSiddet.getSelectedItem().toString()))+""};

        int startLeft=(7*myPageInfo.getPageWidth()/10)+2;

        for (int i=0;i<4;i++){


            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(0);
            canvas.drawRect(startLeft,28,startLeft+15,43,paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);
            paint.setTextSize(2f);
            canvas.drawText(strRısk[i],startLeft ,31,paint);
            paint.setTextSize(5);
            canvas.drawText(strRıskUserr[i],startLeft+5 ,38,paint);
            paint.setTextSize(3f);




            canvas.drawLine(startLeft,32,startLeft+15,32,paint);

            startLeft+=18;

        }

        canvas.drawText("X",(7*myPageInfo.getPageWidth()/10)+18 ,38,paint);
        canvas.drawText("X",(7*myPageInfo.getPageWidth()/10)+36 ,38,paint);
        canvas.drawText("=",(7*myPageInfo.getPageWidth()/10)+54 ,38,paint);
        //paint.settext
        canvas.drawLine(1,44,myPageInfo.getPageWidth()-1,44,paint);
        canvas.drawText("Tehlike Açıklaması",(myPageInfo.getPageWidth()/6)+5 ,49,paint);
        TextPaint mTextPaint=new TextPaint();
        mTextPaint.setTextSize(3);
        mTextPaint.setLetterSpacing(0.15f);
        StaticLayout mTextLayout = new StaticLayout(edAciklamaTehlike.getText().toString(),
                mTextPaint, (canvas.getWidth()/2)-10, Layout.Alignment.ALIGN_NORMAL, 3f, 0.0f, false);
        canvas.save();
        startX = 2;
        startY = 56;
        canvas.translate(startX, startY);
        mTextLayout.draw(canvas);
        canvas.restore();


        canvas.drawText("Fotoğraf",(4*myPageInfo.getPageWidth()/6)+15 ,49,paint);
        canvas.drawLine(1,52,myPageInfo.getPageWidth()-1,52,paint);
        canvas.drawLine(3*myPageInfo.getPageWidth()/6 ,44,3*myPageInfo.getPageWidth()/6,(myPageInfo.getPageHeight()/2)-50,paint);

        if (bitmap!=null){

            Bitmap scaledbitmap3=getResizedBitmap(bitmap,100,100);
            //canvas.drawBitmap(scaledbitmap3,0,0,paint);

        }


        canvas.drawLine(1,(((myPageInfo.getPageHeight()/2)-44)/2)+19,myPageInfo.getPageWidth()/2,(((myPageInfo.getPageHeight()/2)-44)/2)+19,paint);
        canvas.drawText("Risk Açıklaması",(myPageInfo.getPageWidth()/6)+5 ,(((myPageInfo.getPageHeight()/2)-44)/2)+24,paint);
        mTextLayout = new StaticLayout(edAciklamaRisk.getText().toString(),
                mTextPaint, (canvas.getWidth()/2)-10, Layout.Alignment.ALIGN_NORMAL, 3f, 0.0f, false);
        canvas.save();
        startX = 2;
        startY = (((myPageInfo.getPageHeight()/2)-44)/2)+30;
        canvas.translate(startX, startY);
        mTextLayout.draw(canvas);
        canvas.restore();
        canvas.drawLine(1,(((myPageInfo.getPageHeight()/2)-44)/2)+27,myPageInfo.getPageWidth()/2,(((myPageInfo.getPageHeight()/2)-44)/2)+27,paint);



        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(8);
        paint.setLetterSpacing(0.1f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DÜZELTİCİ ÖNLEYİCİ FALİYET",myPageInfo.getPageWidth()/2,(myPageInfo.getPageHeight()/2)-34,paint);
        paint.setTypeface(Typeface.create("Arial", Typeface.NORMAL));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        canvas.drawRect(1,(myPageInfo.getPageHeight()/2)-30,(myPageInfo.getPageWidth())-1,myPageInfo.getPageHeight()-30,paint);
        paint.setStrokeWidth(0);
        canvas.drawRect(5,(myPageInfo.getPageHeight()/2)-25,12,(myPageInfo.getPageHeight()/2)-18,paint);
        canvas.drawRect((myPageInfo.getPageWidth()/2)+5,(myPageInfo.getPageHeight()/2)-25,(myPageInfo.getPageWidth()/2)+12,(myPageInfo.getPageHeight()/2)-18,paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(3f);
        paint.setTextAlign(Paint.Align.LEFT);
        //canvas.save();
        //canvas.scale(1f, 1f);
        canvas.drawText("Düzeltici Faaliyet/Meydana gelmiş bir uygunsuzluk için",13,(myPageInfo.getPageHeight()/2)-20,paint);
        canvas.drawText("Önleyici Faaliyet/Meydana gelebilecek bir uygunsuzluk için",(myPageInfo.getPageWidth()/2)+13,(myPageInfo.getPageHeight()/2)-20,paint);
        paint.setTextSize(4);
        if (chDuzeltici.isChecked())
            canvas.drawText("X",6,(myPageInfo.getPageHeight()/2)-20,paint);
        if (chOnleyici.isChecked())
            canvas.drawText("X",(myPageInfo.getPageWidth()/2)+6,(myPageInfo.getPageHeight()/2)-20,paint);


        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)-17,myPageInfo.getPageWidth()-1,(myPageInfo.getPageHeight()/2)-17,paint);
        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)-8,myPageInfo.getPageWidth()-1,(myPageInfo.getPageHeight()/2)-8,paint);

        canvas.drawText("Alınması Gereken Önlemler",(6*myPageInfo.getPageWidth()/20)+10,(myPageInfo.getPageHeight()/2)-10,paint);
        mTextLayout = new StaticLayout(edAlinmasiGerekenOnlem.getText().toString(),
                mTextPaint, (17*myPageInfo.getPageWidth()/20), Layout.Alignment.ALIGN_NORMAL, 3f, 0.0f, false);
        canvas.save();
        startX = 2;
        startY =(myPageInfo.getPageHeight()/2)-7;
        canvas.translate(startX, startY);
        mTextLayout.draw(canvas);
        canvas.restore();

        canvas.drawLine((17*myPageInfo.getPageWidth()/20)+4,(myPageInfo.getPageHeight()/2)-8,(17*myPageInfo.getPageWidth()/20)+4,
                (myPageInfo.getPageHeight()/2)-8+(myPageInfo.getPageHeight()/8),paint);
        canvas.drawLine((17*myPageInfo.getPageWidth()/20)+10,(myPageInfo.getPageHeight()/2)-8,(17*myPageInfo.getPageWidth()/20)+10,
                (myPageInfo.getPageHeight()/2)-8+(myPageInfo.getPageHeight()/8),paint);

        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)-8+(myPageInfo.getPageHeight()/8),myPageInfo.getPageWidth()-1,
                (myPageInfo.getPageHeight()/2)-8+(myPageInfo.getPageHeight()/8),paint);
        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)+1+(myPageInfo.getPageHeight()/8),myPageInfo.getPageWidth()-1,
                (myPageInfo.getPageHeight()/2)+1+(myPageInfo.getPageHeight()/8),paint);
        canvas.drawText("Uygunsuzluğun Giderilmesi İçin Yapılan Çalışmalar",5*myPageInfo.getPageWidth()/20,(myPageInfo.getPageHeight()/2)-2+(myPageInfo.getPageHeight()/8),paint);

        mTextLayout = new StaticLayout(edUygunsuzlugunGiderilmesi.getText().toString(),
                mTextPaint, (17*myPageInfo.getPageWidth()/20), Layout.Alignment.ALIGN_NORMAL, 3f, 0.0f, false);
        canvas.save();
        startX = 2;
        startY =(myPageInfo.getPageHeight()/2)+1+(myPageInfo.getPageHeight()/8)+1;
        canvas.translate(startX, startY);
        mTextLayout.draw(canvas);
        canvas.restore();

        canvas.drawLine((17*myPageInfo.getPageWidth()/20)+4,(myPageInfo.getPageHeight()/2)+1+(myPageInfo.getPageHeight()/8),(17*myPageInfo.getPageWidth()/20)+4,
                (myPageInfo.getPageHeight()/2)+1+(2*myPageInfo.getPageHeight()/8),paint);
        canvas.drawLine((17*myPageInfo.getPageWidth()/20)+10,(myPageInfo.getPageHeight()/2)+1+(myPageInfo.getPageHeight()/8),(17*myPageInfo.getPageWidth()/20)+10,
                (myPageInfo.getPageHeight()/2)+1+(2*myPageInfo.getPageHeight()/8),paint);
        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)+1+(2*myPageInfo.getPageHeight()/8),myPageInfo.getPageWidth()-1,
                (myPageInfo.getPageHeight()/2)+1+(2*myPageInfo.getPageHeight()/8),paint);


        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        canvas.drawRect(5,(myPageInfo.getPageHeight()/2)+6+(2*myPageInfo.getPageHeight()/8),12,(myPageInfo.getPageHeight()/2)+13+(2*myPageInfo.getPageHeight()/8),paint);
        canvas.drawRect((myPageInfo.getPageWidth()/2)+5,(myPageInfo.getPageHeight()/2)+6+(2*myPageInfo.getPageHeight()/8),(myPageInfo.getPageWidth()/2)+12,
                (myPageInfo.getPageHeight()/2)+13+(2*myPageInfo.getPageHeight()/8),paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("Uygunsuzluk Giderildi",13,(myPageInfo.getPageHeight()/2)+10+(2*myPageInfo.getPageHeight()/8),paint);
        canvas.drawText("Uygunsuzluk Giderilmedi",(myPageInfo.getPageWidth()/2)+13,(myPageInfo.getPageHeight()/2)+10+(2*myPageInfo.getPageHeight()/8),paint);
        paint.setTextSize(4);
        if (chGiderildi.isChecked())
            canvas.drawText("X",6,(myPageInfo.getPageHeight()/2)+10+(2*myPageInfo.getPageHeight()/8),paint);
        else
            canvas.drawText("X",(myPageInfo.getPageWidth()/2)+6,(myPageInfo.getPageHeight()/2)+10+(2*myPageInfo.getPageHeight()/8),paint);
        paint.setTextSize(3f);

        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)+18+(2*myPageInfo.getPageHeight()/8),myPageInfo.getPageWidth()-1,
                (myPageInfo.getPageHeight()/2)+18+(2*myPageInfo.getPageHeight()/8),paint);
        canvas.drawText("            Çalışmayı Yapan                                                      İş Güvenliği Uzmanı                " +
                        "                            Kontrol Eden Ve Onaylayan",13,
                (myPageInfo.getPageHeight()/2)+25+(2*myPageInfo.getPageHeight()/8),paint);
        canvas.drawLine(myPageInfo.getPageWidth()/3,(myPageInfo.getPageHeight()/2)+18+(2*myPageInfo.getPageHeight()/8),myPageInfo.getPageWidth()/3,
                myPageInfo.getPageHeight()-30,paint);
        canvas.drawLine(2*myPageInfo.getPageWidth()/3,(myPageInfo.getPageHeight()/2)+18+(2*myPageInfo.getPageHeight()/8),2*myPageInfo.getPageWidth()/3,
                myPageInfo.getPageHeight()-30,paint);
        canvas.drawLine(1,(myPageInfo.getPageHeight()/2)+30+(2*myPageInfo.getPageHeight()/8),myPageInfo.getPageWidth()-1,
                (myPageInfo.getPageHeight()/2)+30+(2*myPageInfo.getPageHeight()/8),paint);
            paint.setTextSize(2f);
        canvas.drawText("             Ad, Soyad, imza                                                                        Ad, Soyad, imza    " +
                "                                                                    Ad, Soyad, imza",20,(myPageInfo.getPageHeight()/2)+35+(2*myPageInfo.getPageHeight()/8),paint);
        paint.setTextSize(5f);
        canvas.drawText("YS-ISG-017",1,myPageInfo.getPageHeight()-4,paint);

        Calendar calendar=Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(calendar.getTime());
        canvas.drawText("YT-"+date,16*myPageInfo.getPageWidth()/20,myPageInfo.getPageHeight()-4,paint);

        paint.setTextSize(3f);
        canvas.save();
        canvas.rotate(-90);
        canvas.drawText("Termin Tarihi",-228,220,paint);
        canvas.drawText("Tamamlama",-284,220,paint);
        canvas.restore();


        document.finishPage(myPage);

        File file=new File (Environment.getExternalStorageDirectory(),"/Document.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
        }catch (IOException e) {
            e.printStackTrace();
        }

        document.close();
        Toast.makeText(getApplicationContext(),"PDF OLUŞTURULDU",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        controlPermission();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(resizedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return resizedBitmap;
    }
}