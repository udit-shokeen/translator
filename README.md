# 📒 Translator API

**Translator service** built with [Quarkus](https://quarkus.io/).  

It supports 
  - translating a text/paragraph from one language to another,
  - updating/inserting translation for a sentence for given languages, 
  - deleting last added translations for given languages via REST endpoints.

---

## 🚀 Features

- Currently supports in-memory & redis storage of translations
- if source language & target language both are not ENG, then we first translate source to ENG & then ENG to target -- saving memory by not mapping each source language to target language

---

## Endpoints

| Method   | Endpoint                 | Description                                                                    |
| -------- |--------------------------|--------------------------------------------------------------------------------|
| `POST`   | `/translate`             | translate text from sourceLang to targetLang                                   |
| `PUT`    | `/addTranslation`        | add translation for sentence in sourceLang to targetLang mapping               |
| `DELETE` | `/deleteLastTranslation` | delete last added translation for sentence in sourceLang to targetLang mapping |

---

## 📊 Sample Curls

### 1. Translate a text
```bash
curl --location 'http://localhost:8080/translate?sourceLanguage=eng&targetLanguage=hi' \
--header 'Content-Type: application/json' \
--data '{
"text": "Hello. World. Im here"
}'
```
### 2. Add a translation
```bash
curl --location --request PUT 'http://localhost:8080/addTranslation?sourceLanguage=eng&targetLanguage=hi' \
--header 'Content-Type: application/json' \
--data '{
    "text": "Im here",
    "translation": "me aagya"
}'
```
### 3. Delete last added translation
```bash
curl --location --request DELETE 'http://localhost:8080/deleteLastTranslation?sourceLanguage=eng&targetLanguage=hi' \
--header 'Content-Type: application/json' \
--data '{
    "text": "Im here"
}'
```

---

## 📦 Running the Application
### 🔧 Dev Mode (Live Reload)

```bash
./mvnw clean install -DskipTests
./mvnw quarkus:dev

```

---

## FLOW-CHART
