insert into users(username, password, enabled) values ('alumno', '{bcrypt}$2a$04$ndC2bkF33bQsZyNbVph4rObMOxsEQKV41UBkVlYqSrBzyMJRYW5GO', true); /*pass:alumno1 */ 
insert into authorities(username, authority) values ('alumno', 'ROLE_ALUM'); 
insert into users(username, password, enabled) values ('profe', '{bcrypt}$2a$04$oj8uWMqniOT3xOpwd2kQyuXPqBHJql6whf8gT8EI0CnOmt8hLZ5ye', true); /*pass:profe1 */
insert into authorities(username, authority) values ('profe', 'ROLE_PROF');
insert into users(username, password, enabled) values ('admin', '{bcrypt}$2a$04$H/Zekpwdm5yoxXbihy/iUuHawMgmOIY8bMIkfgnE2kAmSAfhFCHNy', true); /*pass:admin1 */ 
insert into authorities(username, authority) values ('admin', 'ROLE_ALUM');
insert into authorities(username, authority) values ('admin', 'ROLE_PROF');