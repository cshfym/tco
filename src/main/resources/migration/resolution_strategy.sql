
-- Build resolution strategies.

SET @NissanMakeId = (SELECT id from `make` where name = 'Nissan');

start transaction;

INSERT INTO `tco_master`.`resolution_strategy` (`id`,`source`,`make_id`,`model_name`,`target_type`,`key_source`,`key`,`value`,`active`)
VALUES
-- Resolve for model name
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Model','Trim','Crew Cab','frontier-crew-cab',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Model','Trim','King Cab','frontier-king-cab',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Titan','Model','Trim','Crew Cab','titan-crew-cab',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Titan','Model','Trim','King Cab','titan-king-cab',1)

-- Resolve for trim name
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Trim','Trim','Desert Runner','desert-runner',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Trim','Trim','PRO-4x','pro-4x',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Trim','Trim','S ','s',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Trim','Trim','SL ','sl',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Frontier','Trim','Trim','SV ','sv',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Titan','Trim','Trim','PRO-4x','pro-4x',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Titan','Trim','Trim','S ','s',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Titan','Trim','Trim','SL ','sl',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Titan','Trim','Trim','SV ','sv',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Xterra','Trim','Trim','Desert Runner','desert-runner',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Xterra','Trim','Trim','PRO-4x','pro-4x',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Xterra','Trim','Trim','S ','s',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Xterra','Trim','Trim','SL ','sl',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Xterra','Trim','Trim','SV ','sv',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Juke','Trim','Trim','NISMO','nismo',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Juke','Trim','Trim','SL ','sl',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Juke','Trim','Trim','SV ','sv',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Armada','Trim','Trim','Platinum','platinum',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Armada','Trim','Trim','S ','s',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Armada','Trim','Trim','SL ','sl',1),
(REPLACE(UUID(),'-',''),'KBB',@NissanMakeId,'Armada','Trim','Trim','SV ','sv',1),
;

commit;

