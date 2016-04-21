LOCK TABLES gpconnect.patients WRITE;

INSERT INTO gpconnect.patients
  (id,    title,   first_name,   last_name,    address_1,                              address_2,            address_3,            postcode,   phone,              date_of_birth, gender,   nhs_number,  pas_number,   department_id, gp_id)
VALUE
  (1,     'Mr',    'Ivor',       'Cox',        '6948 Et St.',                          'Halesowen',          'Worcestershire',     'VX27 5DV', '(011981) 32362',   '1944-06-06',  'Male',   9999999000,  352541,       1,             1),
  (2,     'Mr',    'Ivor',       'Cox',        'Ap #126-6226 Mi. St.',                 'Oakham',             'Rutland',            'XM9 4RF',  '(0112) 740 5408',  '1944-06-06',  'Male',   9999999001,  623454,       1,             2),
  (3,     'Mrs',   'Larissa',    'Mathews',    'P.O. Box 138, 7496 Cursus Avenue',     'Selkirk',            'Selkirkshire',       'X09 3OC',  '0800 1111',        '1937-09-28',  'Female', 9999999002,  346574,       2,             3),
  (4,     'Mr',    'Freya',      'Blackwell',  'P.O. Box 306, 6801 Tellus Street',     'Kirkby Lonsdale',    'Westmorland',        'P32 4GY',  '07624 647524',     '1980-02-03',  'Female', 9999999003,  332546,       1,             4),
  (5,     'Mrs',   'Bertha',     'Case',       'Ap #229-5050 Egestas Avenue',          'St. Albans',         'Hertfordshire',      'B9 8YO',   '0964 934 2028',    '1958-05-14',  'Male',   9999999004,  345267,       3,             5),
  (6,     'Mr',    'Rashad',     'Hill',       '288-6235 Odio. Street',                'Stockton-on-Tees',   'Durham',             'GF7 2OE',  '0909 764 2554',    '1952-02-19',  'Male',   9999999005,  798311,       3,             6),
  (7,     'Mrs',   'Ezra',       'Gordon',     'P.O. Box 711, 8725 Purus Rd.',         'Grangemouth',        'Stirlingshire',      'B4 8MW',   '070 6691 5178',    '1958-06-24',  'Male',   9999999006,  595941,       4,             7),
  (8,     'Mrs',   'Blair',      'Wells',      '783-2544 Cursus, Ave',                 'Lerwick',            'Shetland',           'H2 6HJ',   '(01141) 56013',    '1969-01-31',  'Female', 9999999007,  841652,       4,             8),
  (9,     'Mr',    'Mara',       'Ramos',      '8155 Ac Rd.',                          'Elgin',              'Morayshire',         'Q7U 8CY',  '(026) 5155 5992',  '1938-06-26',  'Female', 9999999008,  685412,       4,             9),
  (10,    'Mr',    'Gisela',     'Dillon',     'P.O. Box 503, 1215 In St.',            'Whithorn',           'Wigtownshire',       'GJ2 2AQ',  '(016977) 0472',    '1981-06-05',  'Female', 9999999009,  584849,       4,             10),
  (11,    'Mr',    'Leslie',     'Sampson',    'Ap #408-5503 Vivamus Rd.',             'Penzance',           'Cornwall',           'EB7K 8ZF', '(01970) 784490',   '1971-06-25',  'Male',   9999999010,  556631,       1,             11),
  (12,    'Mr',    'Hilda',      'Aguirre',    '9460 Sit Av.',                         'Marlborough',        'Wiltshire',          'V15 5FL',  '(0101) 279 8943',  '1986-12-10',  'Female', 9999999011,  645154,       1,             12),
  (13,    'Mr',    'Cameron',    'Monroe',     'P.O. Box 785, 1144 Erat. Rd.',         'Ludlow',             'Shropshire',         'GI1 8ZR',  '076 5024 5403',    '1946-10-04',  'Female', 9999999012,  984313,       2,             13),
  (14,    'Mr',    'Kuame',      'Thompson',   'P.O. Box 649, 8307 Sit Rd.',           'Cawdor',             'Nairnshire',         'GL74 9RY', '0800 252 0881',    '1973-03-14',  'Female', 9999999013,  416166,       3,             14),
  (15,    'Mr',    'Stewart',    'Lynch',      'Ap #540-9476 Nec Avenue',              'Alva',               'Stirlingshire',      'W9J 7NB',  '056 5661 0225',    '1949-10-12',  'Female', 9999999014,  641515,       3,             15),
  (16,    'Mr',    'Hashim',     'Booth',      'P.O. Box 301, 6944 Sapien, St.',       'Bradford',           'Yorkshire',          'WZ8 4PM',  '0353 893 7620',    '1992-11-08',  'Female', 9999999015,  949461,       3,             16),
  (17,    'Mrs',   'Gregory',    'Dillard',    'Ap #728-3451 Dapibus St.',             'Forfar',             'Angus',              'R12 7RK',  '(025) 5608 1829',  '1975-12-27',  'Female', 9999999016,  994512,       4,             17),
  (18,    'Mrs',   'Todd',       'Rocha',      '418-9215 Aliquet Av.',                 'Thurso',             'Caithness',          'VX2M 0UN', '(0113) 310 6232',  '1970-07-14',  'Male',   9999999017,  684135,       4,             18),
  (19,    'Mr',    'Joseph',     'Banks',      '396-7420 Eu, Rd.',                     'Cheltenham',         'Gloucestershire',    'A59 5IO',  '0800 970 8555',    '1984-07-06',  'Female', 9999999018,  943513,       4,             19),
  (20,    'Mrs',   'Xyla',       'Copeland',   '5918 Luctus Rd.',                      'Caerphilly',         'Glamorgan',          'Z8Q 3EU',  '(01326) 24257',    '1944-12-19',  'Female', 9999999019,  684351,       4,             20),
  (21,    'Mr',    'Lysandra',   'Nash',       'P.O. Box 691, 4675 Dictum Rd.',        'Stevenage',          'Hertfordshire',      'AI2 8YN',  '0800 1111',        '1986-08-01',  'Male',   9999999020,  913513,       1,             21),
  (22,    'Mrs',   'Murphy',     'Baird',      '810-6061 Ridiculus Avenue',            'Whitburn',           'West Lothian',       'SL2 2II',  '0800 320 3878',    '1983-06-12',  'Male',   9999999021,  984511,       1,             22),
  (23,    'Mrs',   'Orli',       'Robertson',  'Ap #886-8741 Mauris Street',           'Brighton',           'Sussex',             'H31 1VR',  '0819 258 9104',    '1990-01-11',  'Female', 9999999022,  986413,       2,             23),
  (24,    'Mrs',   'Alden',      'Cote',       '3115 Sit Ave',                         'March',              'Cambridgeshire',     'PW3M 7GS', '0845 46 42',       '1962-05-13',  'Female', 9999999023,  651144,       3,             24),
  (25,    'Mr',    'Harlan',     'Carney',     'P.O. Box 345, 1186 Tristique Street',  'Stockton-on-Tees',   'Durham',             'H7 0IV',   '(016977) 1616',    '1938-02-19',  'Female', 9999999024,  984354,       3,             25),
  (26,    'Mrs',   'Urielle',    'Salinas',    'P.O. Box 391, 9267 Semper Street',     'Mansfield',          'Nottinghamshire',    'DW8 5MU',  '0800 517 2480',    '1988-12-30',  'Female', 9999999025,  643133,       3,             26),
  (27,    'Mr',    'Gabriel',    'Pickett',    '511-1090 Sapien. St.',                 'Balfour',            'Orkney',             'QZ9L 5AT', '0800 1111',        '1979-10-16',  'Female', 9999999026,  843513,       4,             27),
  (28,    'Mr',    'Ryder',      'Guy',        'P.O. Box 442, 1830 Torquent St.',      'Armadale',           'West Lothian',       'T90 9LX',  '0800 1111',        '1992-04-13',  'Male',   9999999027,  894351,       4,             28),
  (29,    'Mr',    'Quentin',    'Cummings',   'P.O. Box 771, 3067 Urna. St.',         'Brampton',           'Cumberland',         'XS3 2HT',  '0845 46 41',       '1948-09-08',  'Male',   9999999028,  986413,       4,             29),
  (30,    'Mrs',   'Seth',       'Downs',      'P.O. Box 456, 3010 Et Ave',            'Hertford',           'Hertfordshire',      'W3E 6DF',  '0845 46 45',       '1960-02-05',  'Male',   9999999029,  213513,       4,             30),
  (31,    'Mrs',   'Sara',       'Combs',      'Ap #169-677 Pede. St.',                'Haverfordwest',      'Pembrokeshire',      'OM5H 8EU', '(01375) 543488',   '1948-12-05',  'Male',   9999999030,  984313,       1,             31),
  (32,    'Mrs',   'Malachi',    'Patel',      '5544 Ante, Street',                    'Hamilton',           'Lanarkshire',        'N06 5LP',  '0800 1111',        '1961-02-04',  'Male',   9999999031,  897454,       1,             32),
  (33,    'Mr',    'Tara',       'Booker',     '616-9384 Mauris Rd.',                  'Northampton',        'Northamptonshire',   'ZY9 2KM',  '(013047) 21772',   '1944-02-20',  'Male',   9999999032,  987684,       2,             33),
  (34,    'Mrs',   'Deborah',    'Wyatt',      '4872 Cubilia St.',                     'Port Glasgow',       'Renfrewshire',       'TQ3J 4JG', '(016977) 0130',    '1965-10-16',  'Female', 9999999033,  987677,       3,             34),
  (35,    'Mrs',   'Doris',      'Santana',    'Ap #491-7493 Donec Ave',               'Newport',            'Hampshire',          'JB48 4EL', '(01774) 13491',    '1933-05-29',  'Female', 9999999034,  955888,       3,             35),
  (36,    'Mr',    'Hadley',     'Peterson',   '7673 Luctus St.',                      'Edinburgh',          'Midlothian',         'OS65 6FU', '0800 1111',        '1980-12-05',  'Female', 9999999035,  561538,       3,             36),
  (37,    'Mrs',   'Desiree',    'Mercer',     '611-682 Sed Road',                     'Taunton',            'Somerset',           'Y92 1WA',  '(020) 1287 4390',  '1933-10-20',  'Male',   9999999036,  684337,       4,             37),
  (38,    'Mr',    'Cassandra',  'Workman',    'Ap #806-2884 Enim St.',                'Auldearn',           'Nairnshire',         'V4F 9GJ',  '(0112) 758 9967',  '1948-03-05',  'Male',   9999999037,  845412,       4,             38),
  (39,    'Mrs',   'Bryar',      'Hendricks',  '549-6333 Magna Street',                'Scalloway',          'Shetland',           'KF3 8IO',  '056 6233 3741',    '1976-08-17',  'Female', 9999999038,  815182,       4,             39),
  (40,    'Mr',    'Kyle',       'Mccarthy',   '668-5984 In St.',                      'Inverness',          'Inverness-shire',    'TX63 6LB', '076 9753 9230',    '1928-12-14',  'Male',   9999999039,  845216,       4,             40),
  (41,    'Mrs',   'Camilla',    'Andrews',    'P.O. Box 684, 9297 Nunc Avenue',       'Nottingham',         'Nottinghamshire',    'IZ7 3BX',  '0800 1111',        '1968-03-14',  'Male',   9999999040,  396641,       5,             41),
  (42,    'Mr',    'James',      'Gilliam',    'P.O. Box 930, 8838 Feugiat St.',       'Newark',             'Nottinghamshire',    'MO63 6WF', '0500 070749',      '1974-09-18',  'Male',   9999999041,  843311,       1,             42),
  (43,    'Mrs',   'Melyssa',    'Scott',      '455-6019 Vel, Rd.',                    'Armadale',           'West Lothian',       'DF42 6NX', '0845 46 49',       '1982-06-18',  'Female', 9999999042,  845141,       2,             43),
  (44,    'Mrs',   'Karly',      'Pennington', '7444 Eget Rd.',                        'Tillicoultry',       'Clackmannanshire',   'XH8 5GC',  '(016977) 1873',    '1974-02-20',  'Female', 9999999043,  843135,       3,             44),
  (45,    'Mr',    'Ali',        'Savage',     'P.O. Box 162, 6180 Dui. St.',          'Wick',               'Caithness',          'OE7 0XY',  '(016977) 9167',    '1945-03-15',  'Male',   9999999044,  984331,       3,             45),
  (46,    'Mr',    'McKenzie',   'Pace',       'P.O. Box 296, 2896 Placerat Road',     'Swindon',            'Wiltshire',          'H5B 9VT',  '07662 731734',     '1966-11-27',  'Female', 9999999045,  986431,       3,             46),
  (47,    'Mr',    'Aiko',       'Griffith',   'P.O. Box 132, 9747 Vestibulum St.',    'Sromness',           'Orkney',             'X2 7XN',   '07304 240742',     '1953-09-14',  'Male',   9999999046,  345484,       4,             47),
  (48,    'Mr',    'MacKenzie',  'Ross',       'Ap #816-3188 Ligula Street',           'Stoke-on-Trent',     'Staffordshire',      'D4G 3ZM',  '0334 839 2867',    '1922-11-15',  'Female', 9999999047,  329974,       4,             48),
  (49,    'Mrs',   'Jonah',      'Mccall',     'Ap #188-3148 Vel St.',                 'Abergele',           'Denbighshire',       'CV25 8ET', '0800 803158',      '1992-09-05',  'Male',   9999999048,  641314,       4,             49),
  (50,    'Mr',    'Carol',      'Griffith',   'Ap #420-1668 Interdum Ave',            'Kilmarnock',         'Ayrshire',           'ZO2U 6IY', '055 8108 4579',    '1927-07-03',  'Male',   9999999049,  684318,       4,             50),
  (51,    'Mrs',   'Avram',      'Miller',     'Ap #804-4028 Phasellus Rd.',           'Ely',                'Cambridgeshire',     'GU67 5CN', '0845 46 47',       '1974-01-25',  'Female', 9999999050,  343941,       5,             51),
  (52,    'Mr',    'Yael',       'Mcfadden',   '3250 Eu, Ave',                         'Crewe',              'Cheshire',           'E4U 1RG',  '0800 1111',        '1946-01-14',  'Female', 9999999051,  841531,       1,             52),
  (53,    'Mrs',   'Sophia',     'Mcneil',     'P.O. Box 710, 4755 Consectetuer St.',  'Eastbourne',         'Sussex',             'O2T 4JX',  '(01768) 59771',    '1925-03-11',  'Male',   9999999052,  686413,       2,             53),
  (54,    'Mrs',   'Rhona',      'Sellers',    '6224 Phasellus Rd.',                   'Newtonmore',         'Inverness-shire',    'Z2U 4BE',  '0500 717497',      '1948-04-05',  'Male',   9999999053,  843511,       3,             54),
  (55,    'Mr',    'Melodie',    'Cooke',      '571-9362 Euismod Ave',                 'Lauder',             'Berwickshire',       'K23 0FE',  '0321 837 1197',    '1957-11-14',  'Male',   9999999054,  684138,       3,             55),
  (56,    'Mrs',   'Xandra',     'Summers',    'Ap #201-1423 Pretium Road',            'Hemel Hempstead',    'Hertfordshire',      'DC05 9IQ', '0845 46 47',       '1947-04-22',  'Female', 9999999055,  684311,       3,             56),
  (57,    'Mrs',   'Hasad',      'Everett',    '241-8659 Metus Avenue',                'Wigtown',            'Wigtownshire',       'A0Y 3OZ',  '(019194) 78089',   '1973-07-31',  'Male',   9999999056,  941344,       4,             57),
  (58,    'Mr',    'Roary',      'Nixon',      'P.O. Box 652, 2912 Non, Street',       'Tullibody',          'Clackmannanshire',   'ZG4 7RA',  '(016516) 39348',   '1952-03-27',  'Female', 9999999057,  864613,       4,             58),
  (59,    'Mr',    'Unity',      'Lawrence',   'Ap #801-9623 Risus Ave',               'Corby',              'Northamptonshire',   'WL46 6RW', '076 5167 2508',    '1955-05-30',  'Male',   9999999058,  846513,       4,             59),
  (60,    'Mr',    'Aretha',     'Hobbs',      '2092 Nullam Avenue',                   'Milton Keynes',      'Buckinghamshire',    'P4S 6GM',  '(0115) 011 9130',  '1922-12-29',  'Male',   9999999059,  313515,       4,             60),
  (61,    'Mrs',   'Aubrey',     'Gallegos',   '2262 Aliquet. Street',                 'Appleby',            'Westmorland',        'V2 2IP',   '07624 328862',     '1928-03-17',  'Male',   9999999060,  941347,       5,             61),
  (62,    'Mr',    'Aaron',      'Christian',  '3868 Cras St.',                        'Weston-super-Mare',  'Somerset',           'A3 9HD',   '(01331) 241623',   '1947-01-27',  'Male',   9999999061,  897431,       1,             62),
  (63,    'Mrs',   'Charissa',   'Spence',     'P.O. Box 357, 2465 Et, St.',           'Roxburgh',           'Roxburghshire',      'S1 0UW',   '(01534) 156770',   '1994-07-30',  'Female', 9999999062,  865115,       2,             63),
  (64,    'Mr',    'Oscar',      'Hubbard',    'P.O. Box 490, 1037 In Street',         'Cwmbran',            'Monmouthshire',      'HV6 6TC',  '0895 189 1046',    '1969-11-18',  'Female', 9999999063,  513154,       3,             64),
  (65,    'Mrs',   'Rashad',     'Lindsey',    'Ap #266-6265 Tempor Road',             'Whithorn',           'Wigtownshire',       'M4 2KW',   '07755 838533',     '1954-10-17',  'Female', 9999999064,  684154,       3,             65),
  (66,    'Mrs',   'Eagan',      'Zamora',     '675-4750 Cursus Rd.',                  'Sutton',             'Surrey',             'VN09 3ZN', '(016977) 7133',    '1947-01-26',  'Male',   9999999065,  584134,       3,             66),
  (67,    'Mr',    'Breanna',    'Becker',     '8380 Ut Av.',                          'Castle Douglas',     'Kirkcudbrightshire', 'UN73 5XU', '056 3920 1956',    '1989-11-02',  'Male',   9999999066,  651598,       4,             67),
  (68,    'Mr',    'Brynne',     'Fuentes',    'P.O. Box 261, 8273 Enim, Ave',         'Paisley',            'Renfrewshire',       'HY6 7UP',  '0800 669413',      '1935-08-13',  'Female', 9999999067,  318494,       4,             68),
  (69,    'Mr',    'Brody',      'Sloan',      'P.O. Box 751, 5005 Dolor Avenue',      'Brampton',           'Cumberland',         'T5W 4JL',  '(01145) 24301',    '1976-09-07',  'Female', 9999999068,  864318,       4,             69),
  (70,    'Mrs',   'Chiquita',   'Todd',       '3589 Sed Ave',                         'Glasgow',            'Lanarkshire',        'OC4 6MH',  '(01852) 01318',    '1989-01-02',  'Female', 9999999069,  651545,       4,             70),
  (71,    'Mr',    'Dante',      'Maddox',     '7305 Est Avenue',                      'Glossop',            'Derbyshire',         'R03 3JH',  '07376 394887',     '1998-02-15',  'Female', 9999999070,  684315,       5,             71),
  (72,    'Mr',    'Beck',       'Mcdonald',   'P.O. Box 264, 2288 Accumsan Rd.',      'Prestatyn',          'Flintshire',         'A6E 6DM',  '(020) 1301 9581',  '1958-07-03',  'Male',   9999999071,  896451,       1,             72),
  (73,    'Mr',    'Shellie',    'Cain',       '5742 Facilisi. Av.',                   'Bathgate',           'West Lothian',       'E9 5XL',   '076 2031 1395',    '1956-10-17',  'Female', 9999999072,  986413,       2,             73),
  (74,    'Mrs',   'Geoffrey',   'Puckett',    'Ap #124-8251 Morbi Road',              'West Ham',           'Essex',              'KC75 7ZB', '055 0916 7547',    '1977-09-12',  'Male',   9999999073,  684354,       3,             74),
  (75,    'Mr',    'Cade',       'Heath',      '233-6719 Nec, Avenue',                 'Campbeltown',        'Argyllshire',        'X60 3CB',  '(016977) 8525',    '1943-03-08',  'Male',   9999999074,  986433,       3,             75),
  (76,    'Mrs',   'Sylvester',  'Wong',       '264-7700 Aenean Rd.',                  'Sromness',           'Orkney',             'Y6 2GQ',   '0816 939 6772',    '1939-04-29',  'Female', 9999999075,  684434,       3,             76),
  (77,    'Mr',    'Gloria',     'Mays',       '2945 Id, Street',                      'Portsoy',            'Banffshire',         'C5 8JN',   '0890 902 2134',    '1998-07-21',  'Female', 9999999076,  894313,       4,             77),
  (78,    'Mr',    'Kim',        'Mcconnell',  '188-267 At, Ave',                      'Bridgwater',         'Somerset',           'IK7O 3EI', '0809 641 0596',    '1995-03-16',  'Male',   9999999077,  864513,       4,             78),
  (79,    'Mr',    'Grace',      'Mcleod',     '6271 Bibendum Av.',                    'Oban',               'Argyllshire',        'YY9L 6SF', '0800 423784',      '1983-02-04',  'Female', 9999999078,  354681,       4,             79),
  (80,    'Mrs',   'Ivy',        'Nunez',      'P.O. Box 931, 4311 Vel Street',        'Basingstoke',        'Hampshire',          'DR0 8TD',  '(01515) 20694',    '1986-12-30',  'Male',   9999999079,  861314,       4,             80),
  (81,    'Mrs',   'Kato',       'Shaw',       '2924 Pretium Av.',                     'Camborne',           'Cornwall',           'J1 3AM',   '07624 601614',     '1969-07-19',  'Male',   9999999080,  684131,       5,             81),
  (82,    'Mr',    'Yvette',     'Melendez',   'Ap #402-2415 Phasellus Road',          'Folkestone',         'Kent',               'A6I 6RG',  '0800 465805',      '1989-12-27',  'Male',   9999999081,  198445,       1,             82),
  (83,    'Mrs',   'Fredericka', 'Cox',        'Ap #507-9742 Lorem Avenue',            'Windsor',            'Berkshire',          'ST1 5QO',  '055 7327 4627',    '1948-07-27',  'Male',   9999999082,  986413,       2,             83),
  (84,    'Mrs',   'Yeo',        'Navarro',    'Ap #817-6953 Enim. Rd.',               'Langholm',           'Dumfriesshire',      'IA2 6DP',  '070 6741 6905',    '1955-01-12',  'Female', 9999999083,  864147,       3,             84),
  (85,    'Mrs',   'Kennedy',    'Stephens',   'Ap #551-2554 Arcu Ave',                'Greenwich',          'Kent',               'J4H 3CR',  '076 5439 2425',    '1975-09-26',  'Male',   9999999084,  651534,       3,             85),
  (86,    'Mrs',   'Plato',      'White',      '193-6144 Euismod Street',              'Kinross',            'Kinross-shire',      'O10 2AB',  '(016977) 7671',    '1988-10-22',  'Female', 9999999085,  584135,       3,             86),
  (87,    'Mrs',   'Jacob',      'Glass',      'Ap #270-3024 A, St.',                  'Jedburgh',           'Roxburghshire',      'R2 7XC',   '0301 776 8869',    '1937-03-30',  'Male',   9999999086,  854144,       4,             87),
  (88,    'Mrs',   'Clio',       'Cooke',      'P.O. Box 624, 2873 Faucibus Ave',      'Folkestone',         'Kent',               'E56 9BB',  '(01027) 44276',    '1964-03-10',  'Female', 9999999087,  643133,       4,             88),
  (89,    'Mrs',   'Aiko',       'Haney',      '323-4504 Tempus Av.',                  'Bedford',            'Bedfordshire',       'U9K 6GK',  '(012913) 24672',   '1954-07-29',  'Female', 9999999088,  841351,       4,             89),
  (90,    'Mr',    'Marcia',     'Hicks',      'Ap #545-9425 Donec St.',               'Tiverton',           'Devon',              'TR1 5VH',  '07954 694224',     '1962-05-03',  'Male',   9999999089,  653518,       4,             90),
  (91,    'Mrs',   'Dante',      'Roth',       'P.O. Box 416, 1038 Nullam Street',     'Laurencekirk',       'Kincardineshire',    'BB1 8NV',  '076 9759 9831',    '1998-03-13',  'Female', 9999999090,  964154,       5,             91),
  (92,    'Mr',    'Zephr',      'Mccarthy',   '390-7981 Lorem Rd.',                   'Clackmannan',        'Clackmannanshire',   'P3O 4EL',  '(01649) 794806',   '1950-06-25',  'Female', 9999999091,  684344,       1,             92),
  (93,    'Mrs',   'Cassidy',    'Wood',       'Ap #166-824 Risus. Ave',               'Tewkesbury',         'Gloucestershire',    'JA2 2DM',  '07624 844651',     '1970-02-19',  'Male',   9999999092,  864313,       2,             93),
  (94,    'Mrs',   'Cooper',     'Irwin',      'Ap #378-7520 Erat, Rd.',               'Halkirk',            'Caithness',          'I5 5QV',   '076 2969 2315',    '1995-11-18',  'Male',   9999999093,  843147,       3,             94),
  (95,    'Mr',    'Gage',       'Meadows',    'P.O. Box 896, 4253 Eleifend St.',      'Tongue',             'Sutherland',         'TG5J 4YC', '(015144) 77262',   '1936-11-04',  'Female', 9999999094,  684311,       3,             95),
  (96,    'Mr',    'Shoshana',   'Hewitt',     '1986 Integer Road',                    'Northampton',        'Northamptonshire',   'TC05 0BW', '070 7594 8865',    '1978-06-14',  'Male',   9999999095,  241843,       3,             96),
  (97,    'Mrs',   'Sade',       'Bowman',     'Ap #163-4448 Nunc. Rd.',               'Bracknell',          'Berkshire',          'L9 5NF',   '07977 658224',     '1954-02-04',  'Female', 9999999096,  841867,       4,             97),
  (98,    'Mr',    'Alexis',     'Greer',      '741-7482 Cras Street',                 'Kilsyth',            'Stirlingshire',      'LW5 1HC',  '0800 719 4863',    '1941-03-23',  'Female', 9999999097,  845135,       4,             98),
  (99,    'Mrs',   'Sarah',      'Fuentes',    'Ap #634-1630 Rutrum Road',             'Annan',              'Dumfriesshire',      'KT3 9HI',  '0500 772606',      '1992-11-09',  'Female', 9999999098,  864177,       4,             99);

UNLOCK TABLES;