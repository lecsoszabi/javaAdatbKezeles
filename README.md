# Bolt Termékkezelő Alkalmazás

Ez a projekt egy **Swing alapú asztali alkalmazás**, amely lehetővé teszi egy bolt termékeinek kezelését egy MySQL adatbázis segítségével. Az alkalmazás támogatja a termékek hozzáadását, törlését, listázását, valamint Excel fájlba történő exportálását.

---

## Funkciók

- **Termékek listázása**: Az adatbázisban tárolt termékek megjelenítése táblázatos formában.
- **Új termék hozzáadása**: Új termékek rögzítése az adatbázisba.
- **Termék törlése**: Egy adott termék törlése az adatbázisból.
- **Összes termék törlése**: Az összes adat törlése az adatbázisból.
- **Random termékek hozzáadása**: Automatikusan generált random termékek felvétele.
- **Excel exportálás**: Az adatbázis tartalmának exportálása Excel (XLSX) fájl formátumba.
- **Lejárati dátum színezése**:
  - *Zöld*: A lejárt termékek.
  - *Kék*: Azon termékek, amelyek az aktuális napon járnak le.
  - *Piros*: Azon termékek, amelyek 30 napon belül lejárnak.

---

## Követelmények

### Szoftverek
- **Java Development Kit (JDK)** 8 vagy újabb
- **MySQL** adatbázis
- **Apache Maven** (opcionális)

### Függőségek
Az alkalmazás az alábbi könyvtárakat használja:
- **Apache POI** (Excel exportáláshoz)
- **Log4j2** (Naplózáshoz)
- **MySQL JDBC Driver**

Maven használata esetén a szükséges függőségek automatikusan letöltődnek. A `pom.xml` fájlban ezek megtalálhatók.

---

## Telepítés és futtatás

1. Klónozd a projektet:
git clone https://github.com/felhasznalo/bolt-termekkezelo.git
cd bolt-termekkezelo
text

2. Hozz létre egy MySQL adatbázist:
```
CREATE DATABASE bolt_db;
USE bolt_db;
CREATE TABLE termekek (
id INT AUTO_INCREMENT PRIMARY KEY,
nev VARCHAR(255) NOT NULL,
darabszam INT NOT NULL,
lejarati_datum DATE NOT NULL
);
```
4. Állítsd be az adatbázis kapcsolatot:
- Nyisd meg a `SwingMySQLApp.java` fájlt.
- Módosítsd az alábbi sorokat a saját MySQL konfigurációd alapján:
  ```
  private static final String url = "jdbc:mysql://localhost:3306/bolt_db";
  private static final String user = "root";
  private static final String password = "";
  ```

4. Futtasd az alkalmazást:
- Maven használata esetén:
  ```
  mvn compile exec:java -Dexec.mainClass="SwingMySQLApp"
  ```
- Ha manuálisan fordítod és futtatod:
  ```
  javac SwingMySQLApp.java
  java SwingMySQLApp
  ```

---

## Használat

1. Indítsd el az alkalmazást. Az ablakban megjelenik egy táblázat és több gomb.
2. Válaszd ki a kívánt műveletet:
- **Termék hozzáadása**: Kattints a "Termék hozzáadása" gombra, és töltsd ki a megjelenő űrlapot.
- **Törlés**: Jelölj ki egy sort a táblázatban, majd kattints a "Törlés" gombra.
- **Minden törlése**: Törli az összes rekordot az adatbázisból.
- **Random termékek hozzáadása**: Automatikusan generál 10 véletlenszerű terméket.
3. Az Excel exportáláshoz kattints az "Exportálás Excelbe" gombra, és válaszd ki a mentési helyet.

---

## Példa képernyőkép
![Screenshot 2024-12-29 151358](https://github.com/user-attachments/assets/bace0937-91f9-4c7f-9f3f-df179a51848a)

---

## Függőségek telepítése manuálisan

Ha nem használsz Maven-t vagy Gradle-t, töltsd le és add hozzá a következő JAR fájlokat a projektedhez:

1. [Apache POI](https://poi.apache.org/download.html)
2. [Log4j2](https://logging.apache.org/log4j/2.x/download.html)
3. [MySQL JDBC Driver](https://dev.mysql.com/downloads/connector/j/)

A JAR fájlokat add hozzá a projekt `classpath`-jához.

---

## Hozzájárulás

Ha szeretnél hozzájárulni ehhez a projekthez:
1. Fork-old ezt a repót.
2. Hozz létre egy új branch-et:
git checkout -b feature/uj-funkcio
text
3. Készítsd el a módosításaidat, majd küldj egy pull request-et.

---

## Licenc

Ez a projekt nyílt forráskódú, és [MIT licenc](LICENSE) alatt érhető el.
