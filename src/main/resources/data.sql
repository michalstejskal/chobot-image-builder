INSERT into network_type (network_type_id, name, image_id) values (90, 'chatbota', 'e');
delete from network_type where network_type_id=90;



-- insert into network_type (network_type_id, name)values (0, 'image');
-- insert into network_type values (1, 'log');
-- insert into network_type values (2, 'chatbot');
--
-- INSERT into network_type (network_type_id, name, image_id) values (0, 'image', 'a');
-- INSERT into network_type (network_type_id, name, image_id) values (1, 'image_preitrained', 'b');
-- INSERT into network_type (network_type_id, name, image_id) values (2, 'log', 'c');
-- INSERT into network_type (network_type_id, name, image_id) values (3, 'log_preitrained', 'd');
-- INSERT into network_type (network_type_id, name, image_id) values (4, 'chatbot', 'e');

-- insert into user values (0, 'stejskys', 'heslo', 'Misa', 'stejskal', 'stejsky.s@mail.com');
-- insert into network (network_id, network_type_id, name, commit_id, status, connection_uri, docker_registry) values (0, 0, 'imagator1', 'commit', 1, 'localhost:5001', 'nejakej registr1');
-- insert into module (name, module_id, type, response_class, code, status, connection_uri, docker_registry, network_id) values ('jmeno_   modulu31', 0, 0, 'response class','ZGVmIGhhbmRsZShkYXRhLCBjb250ZXh0KToKICAgIHByaW50KCJwcmlqYWwganNlbSBkYXRhIikK', 1, 'localhost:5001','nejakej registr1', 0);
-- insert into module_version (module_version_id, name, module_id) values(0, 'nejaka verze', 0);
-- update module set module_version_id=0 where module_id = 0;
--
--
