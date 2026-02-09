---

# ✨ Javadoc Generator Plugin for IntelliJ IDEA

Ein IntelliJ-IDEA-Plugin, das **automatisch präzise Javadoc-Kommentare für Java-Methoden** generiert – mithilfe eines **lokalen LLMs über LM Studio**.
Kein Cloud-Zwang, volle Kontrolle, direkt in deiner IDE.

---

## 🚀 Features

* 🧠 Automatische Javadoc-Generierung für Java-Methoden
* 🤖 Nutzung eines **lokalen LLMs (LM Studio)**
* 🎯 Kurze, präzise und saubere Javadocs
* 🧩 Auswahl einzelner Methoden per IntelliJ-Dialog
* ⚙️ Konfigurierbar über eigenes ToolWindow
* ⛔ Abbruch jederzeit möglich

---

## 🖥️ Voraussetzungen

| Komponente    | Version                    |
| ------------- | -------------------------- |
| IntelliJ IDEA | **2025.2+**                |
| Java          | **JDK 17**                 |
| LM Studio     | aktuell                    |
| LLM-Modell    | z. B. Llama, Mistral, Qwen |

---

## 📦 Installation (ZIP)

1. Lade die Plugin-ZIP-Datei herunter
   *(z. B. `javadoc-generator-plugin.zip`)*

2. Öffne **IntelliJ IDEA**

3. Gehe zu
   **File → Settings → Plugins**

4. Klicke auf das **Zahnrad ⚙️**
   → **Install Plugin from Disk…**

5. Wähle die heruntergeladene **ZIP-Datei**

6. Bestätige und **starte IntelliJ neu**

✅ Das Plugin ist jetzt installiert.

---

## ⚙️ Plugin-Einstellungen

Das Plugin bringt ein eigenes ToolWindow mit:

**View → Tool Windows → Javadoc Generator**

Dort kannst du einstellen:

* **Server URL**
  Standard: `http://localhost:1234`
* **Modell** (automatisch vom LM-Server geladen)
* **Timeouts** (Connect / Write / Read)
* **Detaillevel**

  * Kurz
  * Präzis
  * Ausführlich
* **UI-Optionen**

  * ToolWindow beim Start anzeigen
  * Benachrichtigungen

---

## ✍️ Verwendung

1. Öffne eine **Java-Datei**
2. Platziere den Cursor im Editor
3. Drücke **`Alt + Einfg`**
4. Wähle **Generate Javadocs**
5. Markiere die gewünschten Methoden
6. Bestätige – fertig ✨

Das Plugin:

* analysiert jede ausgewählte Methode
* generiert Javadocs mit dem lokalen LLM
* fügt oder ersetzt vorhandene Kommentare automatisch

---

## 🧠 Generierungsregeln

Die Javadocs werden nach festen Regeln erzeugt:

* 1 Satz zur Beschreibung des Zwecks
* Nur relevante `@param`
* `@return` nur bei Rückgabewert
* Keine Implementierungsdetails
* **Nur Javadoc (`/** ... */`)**, kein Markdown
