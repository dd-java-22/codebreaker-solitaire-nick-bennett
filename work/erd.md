```mermaid
game {
  int game_id PK "Primary key"
  String external_key UK "Unique key, non-null"
  String pool "Code pool, non-null"
  int length "Code length, non-null"
  Instant started "Date-time started, non-null"
  boolean solved "Solved flag, non-null"
  Instant last_played "Date-time of last guess"
  int exact_matches "Exact matches in last guess"
  int near_matches "Near matches in last guess"
}
```