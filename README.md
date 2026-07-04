# Simple Notes — ჩანაწერების აპლიკაცია

Kotlin-ზე დაწერილი მარტივი Android აპლიკაცია, რომელიც აკმაყოფილებს ფინალური გამოცდის ყველა მოთხოვნას: მენიუ, სია, MVVM არქიტექტურა, ბაზასთან კავშირი და ერთი ახალი ფუნქციონალი (Text-to-Speech).

## რას აკეთებს აპლიკაცია

აპლიკაცია საშუალებას აძლევს მომხმარებელს:
- დაამატოს ტექსტური ჩანაწერი (სათაური + შინაარსი)
- ნახოს ყველა ჩანაწერი სიის სახით, ბარათებად
- დაალაგოს ჩანაწერები თარიღით ან სათაურით (ტოპ მენიუდან)
- წაშალოს ჩანაწერი
- **მოუსმინოს ჩანაწერს ხმამაღლა** — ეს არის ახალი ფუნქციონალი, რომელიც კურსში აქამდე არ გამოგვიყენებია (Android-ის Text-to-Speech API)

ყველა მონაცემი ინახება ტელეფონში, ლოკალურ Room ბაზაში — ინტერნეტი საჭირო არ არის.

## ტექნიკური დეტალები

| კომპონენტი | გამოყენებული ტექნოლოგია |
|---|---|
| ენა | Kotlin |
| არქიტექტურა | MVVM (Model – View – ViewModel) |
| ბაზა | Room (ლოკალური SQLite) |
| ასინქრონულობა | Kotlin Coroutines + Flow |
| სია | RecyclerView + ListAdapter + DiffUtil |
| View-სთან წვდომა | **View Binding** (findViewById არსად არ გამოიყენება) |
| მენიუ | Toolbar + MenuProvider (თანამედროვე API, onCreateOptionsMenu-ს ნაცვლად) |
| UI | Material Components (CardView, FloatingActionButton, TextInputLayout) |
| ახალი ფუნქცია | android.speech.tts.TextToSpeech |

### არქიტექტურის ახსნა (MVVM)

**Model** (`data` პაკეტი):
- `Note.kt` — Room Entity, წარმოადგენს ერთ ჩანაწერს
- `NoteDao.kt` — ინტერფეისი SQL მოთხოვნებისთვის (insert, delete, select). Room თავად წერს იმპლემენტაციას.
- `NoteDatabase.kt` — Room ბაზის singleton ინსტანცია
- `NoteRepository.kt` — შუალედური ფენა DAO-სა და ViewModel-ს შორის. სწორედ აქ დაემატებოდა მაგალითად Firebase ან Retrofit, ViewModel-ის შეცვლის გარეშე.

**ViewModel** (`ui/NoteViewModel.kt`):
- ინახავს `LiveData<List<Note>>`-ს, რომელსაც View აკვირდება (observe)
- ამატებს/შლის ჩანაწერს Repository-ს მეშვეობით, კორუტინების (`viewModelScope.launch`) გამოყენებით ბაზასთან მუშაობა UI thread-ს არ ბლოკავს
- გადარჩება ეკრანის შემობრუნებას (configuration change)

**View** (`MainActivity.kt`, `NoteAdapter.kt`, `res/layout/*.xml`):
- მხოლოდ აჩვენებს მონაცემებს და უგზავნის მომხმარებლის მოქმედებებს ViewModel-ს
- იყენებს **View Binding**-ს ყველგან (`ActivityMainBinding`, `ItemNoteBinding`, `DialogAddNoteBinding`) — `findViewById` არსად არ არის გამოყენებული

### ახალი ფუნქციონალი — Text-to-Speech

თითოეულ ჩანაწერს აქვს "დაკვრის" ღილაკი. მასზე დაჭერისას `TextToSpeech` კლასის `speak()` მეთოდი კითხულობს ჩანაწერის სათაურსა და შინაარსს ხმამაღლა, მოწყობილობის ენაზე (`Locale.getDefault()`). ეს API აქამდე კურსში გამოყენებული არ გვქონდა.

## პროექტის სტრუქტურა

```
app/src/main/java/com/example/notesapp/
├── MainActivity.kt
├── data/
│   ├── Note.kt
│   ├── NoteDao.kt
│   ├── NoteDatabase.kt
│   └── NoteRepository.kt
└── ui/
    ├── NoteViewModel.kt
    └── NoteAdapter.kt

app/src/main/res/
├── layout/ (activity_main.xml, item_note.xml, dialog_add_note.xml)
├── menu/menu_main.xml
└── values/ (strings.xml, colors.xml, themes.xml)
```

## როგორ გავუშვათ Android Studio-ში

1. `File → Open` და აირჩიეთ პროექტის ფოლდერი (სადაც `settings.gradle.kts` ფაილია)
2. დაელოდეთ Gradle sync-ს დასრულებას (პირველად ინტერნეტი სჭირდება დამოკიდებულებების ჩამოსატვირთად)
3. თუ Android Studio შემოგთავაზებთ Gradle/AGP/Kotlin-ის ვერსიის განახლებას — დაეთანხმეთ
4. დააჭირეთ ▶ Run ღილაკს ემულატორზე ან რეალურ მოწყობილობაზე (მინიმუმ Android 7.0 / API 24)

დამატებითი კონფიგურაცია არ არის საჭირო — ბაზა ლოკალურია და აპლიკაცია მუშაობს ინტერნეტის გარეშეც.

## შესაძლო გაფართოებები

- ჩანაწერის რედაქტირება (ამჟამად მხოლოდ დამატება/წაშლაა)
- ჩანაწერების ძებნა სათაურით
- ღრუბლოვან სინქრონიზაცია Firebase Realtime Database-ის საშუალებით (Repository ფენა უკვე მზადაა ამისთვის)
