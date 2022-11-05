create table if not exists users
(
    id    BIGINT       not null,
    name  VARCHAR(255) not null,
    email VARCHAR(512) not null,
    constraint USERS_PK
        primary key (id)
);

create unique index if not exists USERS_EMAIL_UINDEX
    on users (email);

create table if not exists items
(
    id              BIGINT       not null,
    name            VARCHAR(255) not null,
    description     VARCHAR(512) not null,
    is_available    BOOLEAN      not null,
    owner_id        BIGINT,
    request_id      BIGINT,
    constraint ITEMS_PK
        primary key (id)
);

create table if not exists bookings
(
    id         BIGINT not null,
    start_date TIMESTAMP,
    end_date   TIMESTAMP,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(10),
    constraint BOOKINGS_PK
        primary key (id)
);

create table if not exists requests
(
    id           BIGINT not null,
    description  VARCHAR(512),
    requestor_id BIGINT,
    constraint REQUESTS_PK
        primary key (id)
);

create table if not exists comments
(
    id        BIGINT not null,
    text      VARCHAR(1000),
    item_id   BIGINT,
    author_id BIGINT,
    constraint COMMENTS_PK
        primary key (id)
);