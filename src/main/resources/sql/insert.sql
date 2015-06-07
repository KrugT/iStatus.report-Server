INSERT INTO `hibernate_sequence` (`next_val`) VALUES ('1000');

INSERT INTO `organistation` (`ID`, `name`, `street`, `zip`, `city`, `created`, `updated`, `version`) VALUES ('10', 'THW OV A', 'Erlenweg 1', '12345', 'Astadt', '2015-01-01', '2015-01-01', '1');
INSERT INTO `organistation` (`ID`, `name`, `street`, `zip`, `city`, `created`, `updated`, `version`) VALUES ('11', 'THW OV B', 'Tannenweg 1', '23451', 'Bstadt', '2015-01-01', '2015-01-01', '1');
INSERT INTO `organistation` (`ID`, `name`, `street`, `zip`, `city`, `created`, `updated`, `version`) VALUES ('12', 'FFW A', 'Birkenweg 1', '12345', 'Astadt', '2015-01-01', '2015-01-01', '1');

INSERT INTO `squad` (`ID`, `name`, `organisation_fk`, `created`, `updated`, `version`) VALUES ('50', 'Gruppe 1', '10', '2015-01-01', '2015-01-01', '1');
INSERT INTO `squad` (`ID`, `name`, `organisation_fk`, `created`, `updated`, `version`) VALUES ('51', 'Gruppe 2', '10', '2015-01-01', '2015-01-01', '1');

INSERT INTO `member` (`ID`, `username`, `givenName`, `surname`, `password`, `organisation_fk`, `created`, `updated`, `version`) VALUES ('100', 'mmuster', 'Max', 'Muster', '12345', '10', '2015-01-01', '2015-01-01', '1');
INSERT INTO `member` (`ID`, `username`, `givenName`, `surname`, `password`, `organisation_fk`, `created`, `updated`, `version`) VALUES ('101', 'mmuster', 'Max', 'Muster', '12345', '10', '2015-01-01', '2015-01-01', '1');

INSERT INTO `member_absence` (`ID`, `begin`, `end`, `member_fk`, `created`, `updated`, `version`) VALUES ('150', '2015-06-01', null, '100', '2015-01-01', '2015-01-01', '1');