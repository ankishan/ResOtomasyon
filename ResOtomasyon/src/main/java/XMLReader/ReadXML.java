package XMLReader;

import android.util.Base64;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import Entity.Departman;
import Entity.Employee;
import Entity.Kategori;
import Entity.MasaDizayn;
import Entity.Menu;
import Entity.MenuM;
import Entity.Urun;
import Entity.UrunlerinListesi;

public class ReadXML {
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = null;
    Document doc = null;
    public String[] masaPlanIsimleri;
    public ArrayList<Employee> lstEmployees;

    public ArrayList<Departman> readDepartmanlar(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        ArrayList<Departman> lstDepartmanlar = new ArrayList<Departman>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("restoran.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    NodeList nList = doc.getElementsByTagName("Restoran");
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node nNode = nList.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Departman d = new Departman();
                            Element element = (Element) nNode;
                            d.DepartmanAdi = element.getElementsByTagName("departmanAdi").item(0).getTextContent();
                            d.DepartmanMenuAdi = element.getElementsByTagName("departmanMenusu").item(0).getTextContent();
                            d.DepartmanEkrani = element.getElementsByTagName("departmanEkrani").item(0).getTextContent();
                            lstDepartmanlar.add(d);
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lstDepartmanlar;
    }


    public ArrayList<MenuM> readMenu(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        ArrayList<MenuM> lstMenuler = new ArrayList<MenuM>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("MenuM.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    NodeList nListKategoriler = doc.getElementsByTagName("Menu");

                    for (int i = 0; i < nListKategoriler.getLength(); i++) {
                        Element nNode = (Element) nListKategoriler.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

//                            Element elementNodeUrunler = (Element) nNode;
                            MenuM m = new MenuM();

                            m.MenuID = Integer.parseInt(nNode.getElementsByTagName("MenuID").item(0).getTextContent());
                            m.MenuAdi = nNode.getElementsByTagName("MenuAdi").item(0).getTextContent();
                            m.Aktif = Boolean.parseBoolean(nNode.getElementsByTagName("Aktif").item(0).getTextContent());
                            lstMenuler.add(m);
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lstMenuler;
    }


    public ArrayList<Kategori> readKategoriler(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        ArrayList<Kategori> lstKategoriler = new ArrayList<Kategori>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("kategoriM.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    NodeList nListKategoriler = doc.getElementsByTagName("Kategori");

                    for (int i = 0; i < nListKategoriler.getLength(); i++) {
                        Element nNode = (Element) nListKategoriler.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

//                            Element elementNodeUrunler = (Element) nNode;
                            Kategori k = new Kategori();

                            k.kategoridID = Integer.parseInt(nNode.getElementsByTagName("kategoriID").item(0).getTextContent());
                            k.kategoriAdi = nNode.getElementsByTagName("kategoriAdi").item(0).getTextContent();
                            k.MenuID = Integer.parseInt(nNode.getElementsByTagName("MenuID").item(0).getTextContent());
                            k.Aktif = Boolean.parseBoolean(nNode.getElementsByTagName("Aktif").item(0).getTextContent());
                            lstKategoriler.add(k);
//                            NodeList nListUrunAdi = ((Element) elementNodeUrunler.getElementsByTagName("urunAdi").item(0)).getElementsByTagName("string");
//                            NodeList nListPorsiyonFiyati = ((Element) elementNodeUrunler.getElementsByTagName("urunPorsiyonFiyati").item(0)).getElementsByTagName("string");
//                            NodeList nListUrunAciklamasi = ((Element) elementNodeUrunler.getElementsByTagName("urunAciklamasi").item(0)).getElementsByTagName("string");
//                            NodeList nListUrunKDV = ((Element) elementNodeUrunler.getElementsByTagName("urunKDV").item(0)).getElementsByTagName("int");
//                            NodeList nListUrunPorsiyonu = ((Element) elementNodeUrunler.getElementsByTagName("urunPorsiyonSinifi").item(0)).getElementsByTagName("int");
//                            NodeList nListUrunTuru = ((Element) elementNodeUrunler.getElementsByTagName("urunTuru").item(0)).getElementsByTagName("string");
//
//                            for (int k = 0; k < nListUrunAdi.getLength(); k++) {
//                                // sadece kilogramla satılan ürünler tablette görünmemeli, kilogram ve porsiyon olan ürünler sadece porsiyon olarak satılacaktır ve görünecektir
//                                if (nListUrunTuru.item(k).getTextContent().contentEquals("Kilogram"))
//                                    continue;
//
//                                UrunlerinListesi u = new UrunlerinListesi();
//
//                                u.urunAdi = nListUrunAdi.item(k).getTextContent();
//                                u.urunFiyati = nListPorsiyonFiyati.item(k).getTextContent();
//                                u.urunAciklamasi = nListUrunAciklamasi.item(k).getTextContent();
//                                u.urunKDV = Integer.parseInt(nListUrunKDV.item(k).getTextContent());
//                                u.urunPorsiyonSinifi = Double.parseDouble(nListUrunPorsiyonu.item(k).getTextContent());
//                                u.urunKategorisi = elementNodeUrunler.getElementsByTagName("kategorininAdi").item(0).getTextContent();
//                                lstUrunler.add(u);
//                            }
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lstKategoriler;
    }

    public ArrayList<Urun> readUrunler(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        ArrayList<Urun> lstUrunler = new ArrayList<Urun>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("UrunlerM.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();

                    NodeList nListUrunler = doc.getElementsByTagName("Urun");

                    for (int i = 0; i < nListUrunler.getLength(); i++) {
                        Element nNode = (Element) nListUrunler.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {

//                            Element elementNodeUrunler = (Element) nNode;
                            Urun u = new Urun();

                            u.UrunKod = Integer.parseInt(nNode.getElementsByTagName("UrunKod").item(0).getTextContent());
                            u.urunAdi = nNode.getElementsByTagName("urunAdi").item(0).getTextContent();
                            u.urunBarkodu = nNode.getElementsByTagName("urunBarkodu").item(0).getTextContent();
                            u.urunKategoriID = Integer.parseInt(nNode.getElementsByTagName("urunKategoriID").item(0).getTextContent());
                            u.urunTuru = nNode.getElementsByTagName("urunTuru").item(0).getTextContent();
                            u.urunFiyati = Double.parseDouble(nNode.getElementsByTagName("urunFiyati").item(0).getTextContent());
                            u.urunKDV = Integer.parseInt(nNode.getElementsByTagName("urunKDV").item(0).getTextContent());
                            u.KDVTip = nNode.getElementsByTagName("KDVTip").item(0).getTextContent();
                            u.urunYazici = nNode.getElementsByTagName("urunYazici").item(0).getTextContent();
                            u.urunAciklamasi = nNode.getElementsByTagName("urunAciklamasi").item(0).getTextContent();
                            u.Aktif = Boolean.parseBoolean(nNode.getElementsByTagName("Aktif").item(0).getTextContent());
                            lstUrunler.add(u);
//                            NodeList nListUrunAdi = ((Element) elementNodeUrunler.getElementsByTagName("urunAdi").item(0)).getElementsByTagName("string");
//                            NodeList nListPorsiyonFiyati = ((Element) elementNodeUrunler.getElementsByTagName("urunPorsiyonFiyati").item(0)).getElementsByTagName("string");
//                            NodeList nListUrunAciklamasi = ((Element) elementNodeUrunler.getElementsByTagName("urunAciklamasi").item(0)).getElementsByTagName("string");
//                            NodeList nListUrunKDV = ((Element) elementNodeUrunler.getElementsByTagName("urunKDV").item(0)).getElementsByTagName("int");
//                            NodeList nListUrunPorsiyonu = ((Element) elementNodeUrunler.getElementsByTagName("urunPorsiyonSinifi").item(0)).getElementsByTagName("int");
//                            NodeList nListUrunTuru = ((Element) elementNodeUrunler.getElementsByTagName("urunTuru").item(0)).getElementsByTagName("string");
//
//                            for (int k = 0; k < nListUrunAdi.getLength(); k++) {
//                                // sadece kilogramla satılan ürünler tablette görünmemeli, kilogram ve porsiyon olan ürünler sadece porsiyon olarak satılacaktır ve görünecektir
//                                if (nListUrunTuru.item(k).getTextContent().contentEquals("Kilogram"))
//                                    continue;
//
//                                UrunlerinListesi u = new UrunlerinListesi();
//
//                                u.urunAdi = nListUrunAdi.item(k).getTextContent();
//                                u.urunFiyati = nListPorsiyonFiyati.item(k).getTextContent();
//                                u.urunAciklamasi = nListUrunAciklamasi.item(k).getTextContent();
//                                u.urunKDV = Integer.parseInt(nListUrunKDV.item(k).getTextContent());
//                                u.urunPorsiyonSinifi = Double.parseDouble(nListUrunPorsiyonu.item(k).getTextContent());
//                                u.urunKategorisi = elementNodeUrunler.getElementsByTagName("kategorininAdi").item(0).getTextContent();
//                                lstUrunler.add(u);
//                            }
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lstUrunler;
    }

    public ArrayList<MenuM> readMenuler(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        ArrayList<MenuM> lstMenuler = new ArrayList<MenuM>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("MenuM.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    NodeList nListMenuler = doc.getElementsByTagName("Menu");
                    for (int i = 0; i < nListMenuler.getLength(); i++) {
                        Node nNode = nListMenuler.item(i);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            MenuM menu = new MenuM();
                            Element elementMenuler = (Element) nNode;

                            menu.MenuAdi = elementMenuler.getElementsByTagName("MenuAdi").item(0).getTextContent();
                            menu.Aktif = Boolean.parseBoolean(elementMenuler.getElementsByTagName("Aktif").item(0).getTextContent());
                            menu.MenuID = Integer.parseInt(elementMenuler.getElementsByTagName("MenuID").item(0).getTextContent());
                            lstMenuler.add(menu);
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lstMenuler;
    }


    public ArrayList<MasaDizayn> readMasaDizayn(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        ArrayList<MasaDizayn> lstMasaDizayn = new ArrayList<MasaDizayn>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("masaDizayn.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    NodeList nListMasaDizayn = doc.getElementsByTagName("MasaDizayn");
                    String[] masaPlanIsmi = new String[nListMasaDizayn.getLength()];
                    for (int i = 0; i < nListMasaDizayn.getLength(); i++) {
                        Node nNodeMasaDizayn = nListMasaDizayn.item(i);
                        if (nNodeMasaDizayn.getNodeType() == Node.ELEMENT_NODE) {
                            Element elementNodeMasaDizayn = (Element) nNodeMasaDizayn;
                            masaPlanIsmi[i] = elementNodeMasaDizayn.getElementsByTagName("masaPlanIsmi").item(0).getTextContent();
                            NodeList nListArrayOfString = elementNodeMasaDizayn.getElementsByTagName("ArrayOfString");
                            for (int j = 0; j < nListArrayOfString.getLength(); j++) {
                                Node nNodeArrayOfString = nListArrayOfString.item(j);
                                Element elementArrayOfString = (Element) nNodeArrayOfString;
                                NodeList nListString = elementArrayOfString.getElementsByTagName("string");
                                for (int k = 0; k < nListString.getLength(); k++) {
                                    MasaDizayn m = new MasaDizayn();
                                    Node nNodeString = nListString.item(k);
                                    if (!nNodeString.getTextContent().contentEquals("")) {
                                        m.MasaAdi = nNodeString.getTextContent();
                                        m.MasaEkraniAdi = elementNodeMasaDizayn.getElementsByTagName("masaPlanIsmi").item(0).getTextContent();
                                        lstMasaDizayn.add(m);
                                    }
                                }
                            }
                        }
                    }
                    masaPlanIsimleri = masaPlanIsmi;
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lstMasaDizayn;
    }

    public String BaseConverter(String text) throws UnsupportedEncodingException {
        byte[] bytes = Base64.decode(text, Base64.DEFAULT);
        return new String(bytes, "UTF-16LE");
    }

    public ArrayList<Employee> readEmployees(List<File> files) {
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        lstEmployees = new ArrayList<Employee>();
        try {
            for (File file : files) {
                String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")
                        + 1);
                if (fileName.contentEquals("tempfiles.xml")) {
                    doc = dBuilder.parse(file);
                    doc.getDocumentElement().normalize();
                    NodeList nListEmployee = doc.getElementsByTagName("UItemp");

                    for (int i = 0; i < nListEmployee.getLength(); i++) {
                        Employee e = new Employee();
                        Node nNodeEmployee = nListEmployee.item(i);
                        if (nNodeEmployee.getNodeType() == Node.ELEMENT_NODE) {
                            Element elementNodeEmployee = (Element) nNodeEmployee;
                            e.UserName = BaseConverter(elementNodeEmployee.getElementsByTagName("UIUN").item(0)
                                    .getTextContent());
                            e.Name = BaseConverter(elementNodeEmployee.getElementsByTagName("UIN").item(0)
                                    .getTextContent());
                            e.LastName = BaseConverter(elementNodeEmployee.getElementsByTagName("UIS").item
                                    (0).getTextContent());
                            e.PassWord = elementNodeEmployee.getElementsByTagName("UIPW").item(0)
                                    .getTextContent();
                            e.Title = BaseConverter(elementNodeEmployee.getElementsByTagName("UIU").item(0)
                                    .getTextContent());
                            e.PinCode = elementNodeEmployee.getElementsByTagName("UIPN").item
                                    (0).getTextContent();

                            Node nNodeEmpPermissions = elementNodeEmployee.getElementsByTagName("UIY").item
                                    (0);
                            Element elementPermissions = (Element) nNodeEmpPermissions;

                            int PermissionArraySize = elementPermissions.getElementsByTagName("string")
                                    .getLength();
                            String[] Permissions = new String[PermissionArraySize];


                            for (int j = 0; j < PermissionArraySize; j++) {
                                Permissions[j] = elementNodeEmployee.getElementsByTagName("string")
                                        .item(j)
                                        .getTextContent();
                            }
                            e.Permissions = Permissions;
                            lstEmployees.add(e);
                        }
                    }
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lstEmployees;

    }
}
