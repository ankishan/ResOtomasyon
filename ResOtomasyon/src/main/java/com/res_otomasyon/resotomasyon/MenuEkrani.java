package com.res_otomasyon.resotomasyon;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import Entity.Kategori;
import Entity.MenuM;
import Entity.Urun;
import Entity.UrunlerinListesi;
import ekclasslar.FileIO;
import ekclasslar.TryConnection;
import ekclasslar.UrunBilgileri;
import Entity.Employee;
import HashPassword.passwordHash;
import XMLReader.ReadXML;


public class MenuEkrani extends ActionBarActivity {

    Menu menu;

    String departmanAdi, masaAdi, siparisler, departmanMenusu;
    private String m_Text = "";
    Employee employee;

    boolean masaKilitliMi = false, passCorrect = false, activityVisible = true, hesapEkraniAcilicak = false;
    Context context = this;
    MenuItem item;
    ArrayList<UrunlerinListesi> urunListesi;
    SharedPreferences preferences = null;
    GlobalApplication g;
    TryConnection t;
    // more efficient than HashMap for mapping integers to objects
    SparseArray<UrunBilgileri> groups = new SparseArray<UrunBilgileri>();
    MyExpandableListAdapter adapter;
    Button buttonMasaAc, buttonSepet;
    ImageView imageMasaAc;
    Boolean masaAcikMi;
    AlertDialog kilitKaldirAlert;
    ExpandableListView expandableListViewMenuEkrani;

    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        try {
                            preferences = context.getSharedPreferences("KilitliMasa", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            if (!masaKilitliMi) {
                                try {
                                    masaKilitliMi = true;
                                    editor.putString("PinCode", employee.PinCode);
                                    editor.putString("Name", employee.Name);
                                    editor.putString("LastName", employee.LastName);
                                    editor.putBoolean("MasaKilitli", masaKilitliMi);
                                    editor.putString("Title", employee.Title);
                                    editor.putString("masaAdi", masaAdi);
                                    editor.putString("departmanAdi", departmanAdi);
                                    Set<String> mySet = new HashSet<String>(Arrays.asList(employee.Permissions));
                                    editor.putStringSet("Permission", mySet);
                                    editor.putString("UserName", employee.UserName);
                                    editor.apply();
                                    item.setTitle(R.string.masa_ac);
                                    if (g.canPlayGame) {
                                        menu.findItem(R.id.templeRun).setVisible(true);
                                    } else {
                                        menu.findItem(R.id.templeRun).setVisible(false);
                                    }
                                    menu.findItem(R.id.action_gorusOneri).setVisible(true);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                passCorrect = passwordHash.validatePassword(m_Text, employee.PinCode);
                                if (passCorrect) {
                                    masaKilitliMi = false;
                                    buttonMasaAc.setVisibility(View.INVISIBLE);
                                    imageMasaAc.setVisibility(View.INVISIBLE);
                                    item.setTitle(R.string.masa_kilitle);
                                    editor.putBoolean("MasaKilitli", masaKilitliMi);
                                    menu.findItem(R.id.templeRun).setVisible(false);
                                    menu.findItem(R.id.action_gorusOneri).setVisible(false);
                                    editor.apply();
                                    kilitKaldirAlert.dismiss();
                                } else {
                                    AlertDialog.Builder aBuilder = new AlertDialog.Builder(MenuEkrani.this);
                                    aBuilder.setTitle("Pin Hatası");
                                    aBuilder.setMessage("Hatalı pin kodu girdiniz")
                                            .setCancelable(false)
                                            .setPositiveButton("Tamam", null);
                                    AlertDialog alertDialog = aBuilder.create();
                                    alertDialog.show();
                                    return;
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case 2:
                    //Server ile bağlantı kurulup kurulmadığını kontrol etmek için gönderilen mesaj.
                    preferences = MenuEkrani.this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    String girisKomutu = "komut=giris&nick=" + preferences.getString("TabletName", "Tablet");

                    if (g.commonAsyncTask.client != null) {
                        if (g.commonAsyncTask.client.out != null) {
                            g.commonAsyncTask.client.sendMessage(girisKomutu);
                            MenuEkrani.this.getSupportActionBar().setTitle(departmanAdi + " - " + masaAdi + "(Bağlı)");
                            buttonSepet.setEnabled(true);
                            SetMenuItemsVisible(true);
                            t.stopTimer();
                        } else {
                            hataVer();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void hataVer() {
        AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
        aBuilder.setTitle("Bağlantı Hatası");
        aBuilder.setMessage("Sunucuya bağlanırken bir hata ile karşılaşıldı. Lütfen tekrar deneyiniz")
                .setCancelable(false)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MenuEkrani.this.finish();
                    }
                });
        AlertDialog alertDialog = aBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        hesapEkraniAcilicak = false;

        if (g == null)
            g = (GlobalApplication) getApplicationContext();
        if (t == null)
            t = new TryConnection(g, myHandler);

        if (!g.commonAsyncTask.client.mRun && !t.timerRunning)
            t.startTimer();

        groups.clear();
        createData();

        expandableListViewMenuEkrani = (ExpandableListView) findViewById(R.id.expandableListViewMenuEkrani);

        expandableListViewMenuEkrani.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int first, int visible, int total) {
                if (expandableListViewMenuEkrani.getTranscriptMode() != 0)
                    expandableListViewMenuEkrani.setTranscriptMode(0);
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        adapter = new MyExpandableListAdapter(this, groups, this, g);
        adapter.bitmapDictionary = g.bitmapDictionary;
        expandableListViewMenuEkrani.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        activityVisible = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        g.isMenuEkraniRunning = false;
        activityVisible = false;
        if (!masaKilitliMi && !hesapEkraniAcilicak) {
            g.siparisListesi.clear();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(g.broadcastReceiverMenuEkrani);
        }
    }

    @Override
    public void onBackPressed() {
        if (!masaKilitliMi) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(g.broadcastReceiverMenuEkrani);
            this.finish();
        }
    }

    public void SetMenuItemsVisible(boolean isVisible) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == R.id.templeRun) {
                if (masaKilitliMi) {
                    if (g.canPlayGame) {
                        menu.findItem(R.id.templeRun).setVisible(true);
                    } else {
                        menu.findItem(R.id.templeRun).setVisible(false);
                    }
                } else
                    menu.getItem(i).setVisible(false);
            } else {
                menu.getItem(i).setVisible(isVisible);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_ekrani);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            g = (GlobalApplication) getApplicationContext();

            if (g.broadcastReceiverMenuEkrani != null) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(g.broadcastReceiverMenuEkrani);
            }
            g.broadcastReceiverMenuEkrani = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String srvrMessage = intent.getStringExtra("message");
//                    srvrMessage = "komut=garsonIstendi&departmanAdi=Departman&masa=RP20";
                    String[] parametreler = srvrMessage.split("&");
                    String[] esitlik;
                    final Dictionary<String, String> collection = new Hashtable<String, String>(parametreler.length);
                    for (String parametre : parametreler) {
                        esitlik = parametre.split("=");
                        if (esitlik.length == 2)
                            collection.put(esitlik[0], esitlik[1]);
                    }
                    String gelenkomut = collection.get("komut");
                    GlobalApplication.Komutlar komut = GlobalApplication.Komutlar.valueOf(gelenkomut);
                    switch (komut) {
                        case baglanti:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (activityVisible) {
                                        if (!t.timerRunning)
                                            t.startTimer();
                                        MenuEkrani.this.getSupportActionBar().setTitle(departmanAdi + " - " + masaAdi + "(Bağlantı yok)");
                                        if (menu != null)
                                            SetMenuItemsVisible(false);
                                        buttonSepet.setEnabled(false);
                                    }
                                }
                            });
                            break;
                        case LoadSiparis:
                            siparisler = collection.get("siparisBilgileri");
                            g.commonAsyncTask.client.sendMessage("komut=OdemeBilgileriTablet&masa=" + masaAdi + "&departmanAdi=" + departmanAdi);
                            break;
                        case OdemeBilgileriTablet:
                            Intent hesapEkrani = new Intent(MenuEkrani.this, HesapEkrani.class);
                            hesapEkrani.putExtra("siparisler", siparisler); // eski siparişler
                            hesapEkrani.putExtra("DepartmanAdi", departmanAdi);
                            hesapEkrani.putExtra("MasaAdi", masaAdi);
                            hesapEkrani.putExtra("Employee", employee);
                            hesapEkrani.putExtra("alinanOdemeler", collection.get("alinanOdemeler"));
                            hesapEkrani.putExtra("indirimler", collection.get("indirimler"));
                            hesapEkrani.putExtra("MasaAcikMi", masaAcikMi);
                            hesapEkraniAcilicak = true;
                            startActivity(hesapEkrani);
                            break;
                        case masaKapandi:
                            if (collection.get("masa").contentEquals(masaAdi) && collection.get("departmanAdi").contentEquals(departmanAdi)) {
                                g.siparisListesi.clear();
                                masaAcikMi = false;
                                if (masaKilitliMi) {
                                    groups.clear();
                                    createData();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                            //resim ve buton visible yapılacak
                                            buttonMasaAc.setVisibility(View.VISIBLE);
                                            imageMasaAc.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } else {
                                    onBackPressed();
                                }
                            }
                            break;
                        case masaAcildi:
                            if (collection.get("masa").contentEquals(masaAdi) && collection.get("departmanAdi").contentEquals(departmanAdi)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //resim ve button invisible yapılacak
                                        buttonMasaAc.setVisibility(View.INVISIBLE);
                                        imageMasaAc.setVisibility(View.INVISIBLE);
                                    }
                                });
                                masaAcikMi = true;
                            }
                        default:
                            break;
                    }

                }
            };
            LocalBroadcastManager.getInstance(context).registerReceiver(g.broadcastReceiverMenuEkrani, new IntentFilter("myevent"));
        } catch (Exception ignored) {

        }


        Bundle extras = getIntent().getExtras();
        departmanAdi = extras.getString("DepartmanAdi");
        masaAdi = extras.getString("MasaAdi");
        employee = (Employee) extras.getSerializable("Employee");
        masaAcikMi = extras.getBoolean("MasaAcikMi");
        departmanMenusu = extras.getString("departmanMenusu");

        FileIO fileIO = new FileIO();
        List<File> files = null;

        preferences = this.getSharedPreferences("KilitliMasa", Context.MODE_PRIVATE);
        masaKilitliMi = preferences.getBoolean("MasaKilitli", false);

        buttonMasaAc = (Button) findViewById(R.id.buttonMasaAc);
        imageMasaAc = (ImageView) findViewById(R.id.imageView);

        if (masaKilitliMi && !masaAcikMi) {
            buttonMasaAc.setVisibility(View.VISIBLE);
            imageMasaAc.setVisibility(View.VISIBLE);
        }

        buttonMasaAc.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                buttonMasaAc.setVisibility(View.INVISIBLE);
                imageMasaAc.setVisibility(View.INVISIBLE);
                g.commonAsyncTask.client.sendMessage("komut=masaAcildi&masa=" + masaAdi + "&departmanAdi=" + departmanAdi);
                g.commonAsyncTask.client.sendMessage("komut=masayiAc&masa=" + masaAdi + "&departmanAdi=" + departmanAdi);
            }
        });

        try {
            files = fileIO.getListFiles(new File(Environment.getExternalStorageDirectory().getPath() + "/shared/Lenovo"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ReadXML readXml = new ReadXML();
        ArrayList<Urun> lstUrunler = readXml.readUrunler(files);
        ArrayList<Kategori> lstKategoriler = readXml.readKategoriler(files);
        ArrayList<MenuM> lstMenuler = readXml.readMenu(files);
        urunListesi = new ArrayList<UrunlerinListesi>();
        for (int i = 0; i < lstUrunler.size(); i++) {
            UrunlerinListesi urunListe = new UrunlerinListesi();
            urunListe.urunAdi = lstUrunler.get(i).urunAdi;
            urunListe.urunAciklamasi = lstUrunler.get(i).urunAciklamasi;
            urunListe.urunFiyati = String.valueOf((lstUrunler.get(i).urunFiyati));
            String kategoriAdi = "";
            for (int k = 0; k < lstKategoriler.size(); k++) {
                if (lstKategoriler.get(k).kategoridID == lstUrunler.get(i).urunKategoriID) {
                    kategoriAdi = lstKategoriler.get(k).kategoriAdi;
                }
            }
            urunListe.urunKategorisi = kategoriAdi;
            urunListe.urunKDV = lstUrunler.get(i).urunKDV;
            urunListe.urunPorsiyonSinifi = 2.0;
            urunListesi.add(urunListe);
        }
//        urunListesi = readUrun.readUrunler(files);
        if (g.commonAsyncTask.client != null) {
            if (g.commonAsyncTask.client.out != null) {
                MenuEkrani.this.getSupportActionBar().setTitle(departmanAdi + " - " + masaAdi + "(Bağlı)");
            } else {
                MenuEkrani.this.getSupportActionBar().setTitle(departmanAdi + " - " + masaAdi + "(Bağlantı yok)");
            }
        }
        t = new TryConnection(g, myHandler);

        buttonSepet = (Button) findViewById(R.id.buttonSepet);

        buttonSepet.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                g.commonAsyncTask.client.sendMessage("komut=LoadSiparis&masa=" + masaAdi + "&departmanAdi=" + departmanAdi);
             }
        });
    }

    @Override
    protected void onStop() {
        g.isMenuEkraniRunning = false;
        activityVisible = false;
        if (!masaKilitliMi && !hesapEkraniAcilicak) {
            g.siparisListesi.clear();
            LocalBroadcastManager.getInstance(this).unregisterReceiver(g.broadcastReceiverMenuEkrani);
        }
        if (t.timerRunning)
            t.stopTimer();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        activityVisible = false;
        g.isMenuEkraniRunning = false;
        if (!masaKilitliMi && !hesapEkraniAcilicak) {
            g.siparisListesi.clear();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(g.broadcastReceiverMenuEkrani);
        super.onDestroy();
    }

    public void createData() {
        int j = 0;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);

        FileIO fileIO = new FileIO();
        List<File> files = null;

        try {
            files = fileIO.getListFiles(new File(Environment.getExternalStorageDirectory().getPath() + "/shared/Lenovo"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ReadXML readMenuler = new ReadXML();
        ArrayList<Entity.MenuM> menuler = readMenuler.readMenuler(files);
        ArrayList<Kategori> lstKategoriler = readMenuler.readKategoriler(files);

        for (Entity.MenuM aMenuler : menuler) {
            if (aMenuler.MenuAdi.contentEquals(departmanMenusu)) {
                for (int k = 0; k < lstKategoriler.size(); k++) {
                    int ilkUrununIndexi = -1;
                    UrunBilgileri group = new UrunBilgileri(lstKategoriler.get(k).kategoriAdi);
                    for (int l = 0; l < urunListesi.size(); l++) {
                        if (urunListesi.get(l).urunKategorisi.contentEquals(lstKategoriler.get(k).kategoriAdi)) {
                            ilkUrununIndexi = l;
                            break;
                        }
                    }
                    if (ilkUrununIndexi != -1) {
                        while (urunListesi.get(ilkUrununIndexi).urunKategorisi.contentEquals(lstKategoriler.get(k).kategoriAdi)) {
                            group.productName.add(urunListesi.get(ilkUrununIndexi).urunAdi);
                            group.productPrice.add(urunListesi.get(ilkUrununIndexi).urunFiyati);
                            group.productInfo.add(urunListesi.get(ilkUrununIndexi).urunAciklamasi);
                            group.productPortionClass.add(urunListesi.get(ilkUrununIndexi).urunPorsiyonSinifi);

                            String miktar = "0";
                            for (int m = 0; m < g.siparisListesi.size(); m++) {
                                if (g.siparisListesi.get(m).siparisYemekAdi.contentEquals(urunListesi.get(ilkUrununIndexi).urunAdi)) {
                                    miktar = df.format(Double.parseDouble(miktar) + g.siparisListesi.get(m).siparisAdedi * g.siparisListesi.get(m).siparisPorsiyonu);
                                }
                            }

                            group.productCount.add(miktar);
                            ilkUrununIndexi++;
                            if (ilkUrununIndexi >= urunListesi.size())
                                break;
                        }
                    } else {
                        continue;
                    }
                    groups.append(j, group);
                    j++;
                }
//                for (int k = 0; k < aMenuler.MenununKategorileri.size(); k++) {

//                    UrunBilgileri group = new UrunBilgileri(aMenuler.MenununKategorileri.get(k));

//                    int ilkUrununIndexi = -1;
//
//                    for (int l = 0; l < urunListesi.size(); l++) {
//                        if (urunListesi.get(l).urunKategorisi.contentEquals(aMenuler.MenununKategorileri.get(k))) {
//                            ilkUrununIndexi = l;
//                            break;
//                        }
//                    }
//
//                    if (ilkUrununIndexi != -1) {
//                        while (urunListesi.get(ilkUrununIndexi).urunKategorisi.contentEquals(aMenuler.MenununKategorileri.get(k))) {
//                            group.productName.add(urunListesi.get(ilkUrununIndexi).urunAdi);
//                            group.productPrice.add(urunListesi.get(ilkUrununIndexi).urunFiyati);
//                            group.productInfo.add(urunListesi.get(ilkUrununIndexi).urunAciklamasi);
//                            group.productPortionClass.add(urunListesi.get(ilkUrununIndexi).urunPorsiyonSinifi);
//
//                            String miktar = "0";
//                            for (int m = 0; m < g.siparisListesi.size(); m++) {
//                                if (g.siparisListesi.get(m).siparisYemekAdi.contentEquals(urunListesi.get(ilkUrununIndexi).urunAdi)) {
//                                    miktar = df.format(Double.parseDouble(miktar) + g.siparisListesi.get(m).siparisAdedi * g.siparisListesi.get(m).siparisPorsiyonu);
//                                }
//                            }
//
//                            group.productCount.add(miktar);
//                            ilkUrununIndexi++;
//                            if (ilkUrununIndexi >= urunListesi.size())
//                                break;
//                        }
//                    } else {
//                        continue;
//                    }
//                    groups.append(j, group);
//                    j++;
//                }
                break;
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ekrani, menu);
        this.menu = menu;
        preferences = this.getSharedPreferences("KilitliMasa", Context.MODE_PRIVATE);
        masaKilitliMi = preferences.getBoolean("MasaKilitli", false);
//        masaKilitliMi = context.getSharedPreferences("KilitliMasa", Context.MODE_PRIVATE).getBoolean
//                ("MasaKilitli", masaKilitliMi);
        if (masaKilitliMi) {
            menu.findItem(R.id.action_gorusOneri).setVisible(true);
            this.item = menu.findItem(R.id.action_lockTable);
            item.setTitle(R.string.masa_ac);
            if (g.canPlayGame) {
                menu.findItem(R.id.templeRun).setVisible(true);
            } else {
                menu.findItem(R.id.templeRun).setVisible(false);
            }
        } else {
            menu.findItem(R.id.templeRun).setVisible(false);
            menu.findItem(R.id.action_gorusOneri).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        this.item = item;
        String komut;
        switch (id) {
            case R.id.action_lockTable:
                if (item.getTitle().toString().contentEquals("Masayı Kilitle")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Masayı Kilitle");
                    builder.setPositiveButton("Masayı Kilitle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myHandler.sendEmptyMessage(0);
                            if (!masaAcikMi) {
                                buttonMasaAc.setVisibility(View.VISIBLE);
                                imageMasaAc.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    builder.setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
//                    return false;
                } else if (item.getTitle().toString().contentEquals("Masa Kilidini Kaldır")) {


                    final EditText input = new EditText(context);
                    input.setHint("Şifre");
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

                    kilitKaldirAlert = new AlertDialog.Builder(context)
                            .setTitle("Masa kilidini kaldır")
                            .setView(input)
                            .setCancelable(false)
                            .setPositiveButton("Kilidi Kaldır", null)
                            .setNegativeButton("Vazgeç", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create();

                    kilitKaldirAlert.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {

                            final Button positiveButton = kilitKaldirAlert.getButton(AlertDialog.BUTTON_POSITIVE);

                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    m_Text = input.getText().toString();
                                    myHandler.sendEmptyMessage(0);
                                }
                            });
                        }
                    });

                    kilitKaldirAlert.show();
                }
                break;

            case R.id.action_masaTemizleyin:
                komut = "komut=TemizlikIstendi&departmanAdi=" + departmanAdi + "&masa=" + masaAdi;
                g.commonAsyncTask.client.sendMessage(komut);
                break;

            case R.id.action_gorusOneri:
                Intent intent = new Intent(MenuEkrani.this, SurveyScreen.class);
                startActivity(intent);
                break;

            case R.id.action_garsonIstiyorum:
                komut = "komut=GarsonIstendi&departmanAdi=" + departmanAdi + "&masa=" + masaAdi;
                g.commonAsyncTask.client.sendMessage(komut);
                break;
            case R.id.templeRun:
                if (g.canPlayGame) {
                    Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.imangi.templerun2");
                    startActivity(LaunchIntent);
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}