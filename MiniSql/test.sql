 create table book (bno int, bname char(8) unique, price int, primary key(bno));

 insert into book values(1, 'xxxx', 100);
 insert into book values(2, 'xcl', 200);
  insert into book values(3, 'ccc', 300);
   insert into book values(4, 'llll', 400);
   create index mybno on book(bno);
    create index mybname on book(bname);
    
 create table class (place char(20), studenteNum float, id int , primary key(id));
 create index myplace on class(place);
 insert into class values('No1Street',50,100);
 insert into class values('No2Street',200,400);
  insert into class values('No3Street',500,1000);
