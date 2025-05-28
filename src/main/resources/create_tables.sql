create table if not exists post(
    id bigserial primary key,
    name varchar(250) not null,
    picture bytea,
    post_text text,
    tags varchar(4000),
    likes_count integer default 0);

create table if not exists comment(
    id bigserial primary key,
    comment_text text,
    post_id bigint,
    foreign key (post_id) references post (id) on delete cascade);