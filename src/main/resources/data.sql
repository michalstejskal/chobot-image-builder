insert into network_type values (0, 'image');
insert into network_type values (1, 'log');
insert into network_type values (2, 'chatbot');

insert into user values (0, 'stejskys', 'heslo', 'Misa', 'stejskal', 'stejsky.s@mail.com');
insert into network (network_id, network_type_id, name, tag, status, connection_uri, docker_registry) values (0, 0, 'imagator1', 'tag site', 1, 'localhost:5001', 'nejakej registr1');
insert into module (name, module_id, type, response_class, code, status, connection_uri, docker_registry, network_id) values ('jmeno_   modulu31', 0, 0, 'response class','ZGVmIGhhbmRsZShkYXRhLCBjb250ZXh0KToKICAgIHByaW50KCJwcmlqYWwganNlbSBkYXRhIikK', 1, 'localhost:5001','nejakej registr1', 0);
insert into module_version (module_version_id, name, module_id) values(0, 'nejaka verze', 0);
update module set module_version_id=0 where module_id = 0;
-- INSERT into user_application values (0, 'nejakej name', 'nejaka desc', 0, 0, 0);

