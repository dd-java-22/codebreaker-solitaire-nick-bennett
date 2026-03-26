```mermaid
game {
  int game_id PK "Primary key, auto-numbered"
  String external_key UK1 "Unique key, non-null"
  String pool "Code pool, non-null"
  int length IX2 "Code length, non-null"
  Instant started IX1 "Date-time started, non-null"
  int guess_count IX2 "Number of guesses"
  boolean solved IX1 "Solved flag, non-null"
  Instant last_played IX1 "Date-time of last guess"
  int exact_matches "Exact matches in last guess"
  int near_matches "Near matches in last guess"
}
```