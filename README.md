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
- **Apache Maven** (opcionális, ha Maven-t használsz)

### Függőségek
Az alkalmazás az alábbi könyvtárakat használja:
- **Apache POI** (Excel exportáláshoz)
- **Log4j2** (Naplózáshoz)
- **MySQL JDBC Driver**

Maven használata esetén a szükséges függőségek automatikusan letöltődnek. A `pom.xml` fájlban ezek megtalálhatók.

---

## Telepítés és futtatás

1. Klónozd a projektet:
