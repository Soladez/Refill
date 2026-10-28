# Refill

Clientseitige Fabric-Mod für **Minecraft 26.3**. Sie füllt Items, die in der Hand ausgehen, automatisch aus dem Inventar nach.

## Funktionen

- **Haupthand und Nebenhand:** Ist der letzte Block gesetzt, das letzte Essen gegessen oder ein Werkzeug zerbrochen, kommt automatisch ein gleicher Stapel aus dem Inventar in den Slot.
- **Bevorzugt das Hauptinventar:** Erst danach werden andere Hotbar-Slots benutzt, damit deine Hotbar-Ordnung erhalten bleibt.
- **Werkzeuge, Waffen und Rüstung:** Auch Exemplare mit anderer Haltbarkeit oder anderen Verzauberungen zählen als Ersatz.
- **Kein Nachfüllen in diesen Fällen:**
  - wenn du den Stapel absichtlich wegwirfst (Q)
  - wenn du ihn mit F in die andere Hand tauschst
  - wenn ein Inventar oder ein Menü offen ist
  - im Kreativ- und im Zuschauermodus
- **Funktioniert auch auf Servern:** Es werden nur normale Inventar-Klicks gesendet, so als würdest du im Inventar eine Hotbar-Taste drücken.

## Steuerung

| Taste | Aktion |
|---|---|
| **ALT GR** | Auto-Nachfüllen ein/aus (Anzeige über der Hotbar) |

Die Taste kannst du unter *Optionen → Steuerung → Tastenbelegung → Refill* ändern. Der Zustand wird in `config/refill.properties` gespeichert und ist standardmäßig **an**.

## Installation

1. Installiere [Fabric Loader](https://fabricmc.net/use/) für Minecraft 26.3 (Version 0.19.5 oder neuer).
2. Lege [Fabric API](https://modrinth.com/mod/fabric-api) und `refill-<version>.jar` in den Ordner `mods`.

Die fertige JAR-Datei findest du in zwei Stellen:
- unter [Releases](https://github.com/Soladez/Refill/releases)
- als Artefakt `refill-mod` im jeweiligen Lauf unter [Actions](https://github.com/Soladez/Refill/actions)

## Selbst bauen

Dafür brauchst du Java 25.

```bash
./gradlew build
```

Die JAR-Datei liegt danach in `build/libs/`.

## Neues Release

```bash
git tag v1.0.0 && git push origin v1.0.0
```

GitHub Actions baut die Mod dann und hängt die JAR-Datei an ein Release an.
