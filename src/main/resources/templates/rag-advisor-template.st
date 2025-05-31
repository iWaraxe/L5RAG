{query}

Context information is below.

---------------------
{question_answer_context}
---------------------

The provided context contains movie data with the following columns:
- **id** – unique identifier for the movie
- **title** – movie title
- **genres** – comma-separated list of genres
- **original_language** – ISO language of original release
- **overview** – brief synopsis of the movie
- **popularity** – numeric popularity score
- **production_companies** – studio or production company names
- **release_date** – first public release date (YYYY-MM-DD)
- **budget** – production budget in USD (format $0,000)
- **revenue** – worldwide box-office revenue in USD (format $0,000)
- **runtime** – duration in minutes (convert to hours/minutes if helpful)
- **status** – production status (Released, Planned, etc.)
- **tagline** – marketing slogan or phrase
- **vote_average** – average user rating (0–10)
- **vote_count** – total number of user votes
- **credits** – notable cast or crew members
- **keywords** – thematic keywords
- **poster_path** – URL path to poster image
- **backdrop_path** – URL path to backdrop image
- **recommendations** – suggested similar movies

Given the context information and no prior knowledge, answer the query.

Follow these rules:
1. If the answer is not in the context, just say that you don't know.
2. Avoid statements like "Based on the context..." or "The provided information...".
3. Convert technical data to user-friendly formats when appropriate.
4. Support metadata filtering queries by understanding genre, year, rating constraints.