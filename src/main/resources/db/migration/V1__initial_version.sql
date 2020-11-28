create extension if not exists "uuid-ossp";


create table genres (
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  name text NOT NULL,

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  PRIMARY KEY(id)
);

create table books (
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  name text NOT NULL,
  author_id UUID NOT NULL,
  genre_id UUID NOT NULL REFERENCES genres(id),
  published_year INTEGER,
  pages_count INTEGER,

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  CHECK (pages_count > 0),
  CHECK (published_year > -5000),
  PRIMARY KEY(id)
);

create table authors (
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  name text NOT NULL,

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  PRIMARY KEY(id)
);

create table authors_genres(
  id UUID NOT NULL DEFAULT uuid_generate_v4(),
  author_id UUID NOT NULL REFERENCES authors(id),
  genre_id UUID NOT NULL REFERENCES genres(id),

  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  PRIMARY KEY(id),

  UNIQUE(author_id, genre_id)
);


insert into genres(name, created_at, updated_at) values
 ('programming', NOW(), NOW()),
 ('horror', NOW(), NOW()),
 ('no', NOW(), NOW());
