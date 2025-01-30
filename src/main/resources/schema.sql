create table if not exists chat_memory (
    id serial primary key,
    conversation_id varchar,
    message_type varchar,
    content varchar
);

create index if not exists chat_memory_conversation_id
on chat_memory ( conversation_id );

create table if not exists shopping_history (
    id serial primary key,
    purchased_date date not null,
    purchased_item varchar not null,
    payment_amount integer not null
);