# BoardGameCollector - Aplikacja do przechowywania kolekcji gier planszowych

BoardGameCollector (BGC) to aplikacja, która umożliwia przechowywanie informacji o kolekcji gier planszowych. Aplikacja synchronizuje listę gier z serwisem BoardGameGeek (BGG), pozwalając użytkownikowi na rzeczywisty podgląd informacji o grach znajdujących się w kolekcji.

## Wymagania techniczne

Aplikacja została stworzona z wykorzystaniem następujących technologii i narzędzi:

- Baza danych: SQLite
- Środowisko programistyczne: Android Studio (API 27)
- Język programowania: Kotlin
- API: BoardGameGeek (BGG)

## Funkcjonalności aplikacji

### Ekran logowania

Aplikacja umożliwia synchronizację listy gier z serwisem BoardGameGeek (BGG). Po uruchomieniu aplikacji, należy się zalogować podając nazwę użytkownika. Jeśli użytkownik o danej nazwie istnieje, następuje zalogowanie (zostaje stworzona baza danych zawierająca informacje o grach oraz dodatkach w kolekcji użytkownika); pojawia się również komunikat potwierdzający wykonywaną operację pobierania danych.

### Ekran główny

Po zalogowaniu widzimy podgląd ogólny na ilość gier, dodatków, datę i godzinę ostatniej synchronizacji oraz przyciski, które umożliwiają wejście w aktywność zawierającą szczegóły gier lub dodatków. Możemy również dokonać synchronizacji lub się wylogować. Przy wybraniu któregoś z tych dwóch przycisków, aplikacja dodatkowo zapyta o potwierdzenie wykonania operacji. Po wybraniu opcji "Synchronizuj", pobierze ona aktualną kolekcję gier użytkownika z BGG i zaktualizuje bazę danych w aplikacji. Aplikacja równie informuje na bieżąco o wykonywanych działaniach (np. przeprowadzonej pomyślnie synchronizacji).

### Ekran szczegółów gier i dodatków

Oba ekrany zawierają te same funkcjonalności. Wyświetlają odpowiednio listę posiadanych gier/dodatków (nr id, miniaturkę zdjęcia, tytuł oraz rok wydania). Istnieje możliwość posortowania listy według tytułów lub względem roku wydania. Po wybraniu konkretnej gry/dodatku możemy przejść do widoku szczegółowego.

### Ekran szczegółowy konkretnej gry/dodatku

Aktywność zawiera podstawowe informacje (nr id, miniaturkę zdjęcia, tytuł oraz rok wydania) oraz dodatkowo zawiera opcję dodania (lub załadowania z galerii urządzenia) zdjęć i przypisania ich do danej gry/dodatku.
