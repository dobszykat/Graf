<?php

error_reporting(E_ALL);
set_time_limit(0);

ob_implicit_flush();

$address = '0.0.0.0';
$port    = 7452;
$mysqli = new mysqli('localhost:3306', 'root', '', 'grafologus');

if ($mysqli->connect_error) {
    die('Connect Error (' . $mysqli->connect_errno . ') '
            . $mysqli->connect_error);
}
echo "Adatbazis csatlakozas sikeres!\n";

if (($sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) === false) {
    echo "socket_create() falló: razón: " . socket_strerror(socket_last_error()) . "\n";
}

if (socket_bind($sock, $address, $port) === false) {
    echo "socket_bind() falló: razón: " . socket_strerror(socket_last_error($sock)) . "\n";
}

if (socket_listen($sock, 5) === false) {
    echo "socket_listen() falló: razón: " . socket_strerror(socket_last_error($sock)) . "\n";
}


$clients         = array();	//Kliensek tömbje
$grafologusok    = array(); //Grafológusok tombje
$grafologusnevek = array();
$paciensek       = array(); //Páciensek tombje
$paciensnevek    = array();

$grafologus = 0;
$paciens    = 0;
$grafszam   = -2;
$pacszam    = -2;
$connection = false;
$gkey       = -8;
$pkey       = -8;

do {
    $read   = array();
    $read[] = $sock;
    
    $read = array_merge($read, $clients);
    
    if (socket_select($read, $write, $except, $tv_sec = 5) < 1) {
          //  SocketServer::debug("Probléma a blokkolásnál.");
        continue;
    }
    
    // Új kapcsolatok kezelése
    if (in_array($sock, $read)) {
        
        if (($msgsock = socket_accept($sock)) === false) {
            echo "socket_accept() hiba: " . socket_strerror(socket_last_error($sock)) . "\n";
            break;
        }
        $clients[] = $msgsock;
        $key       = array_keys($clients, $msgsock);
    }
	
    // Beérkezõ üzenetek kezelése
    foreach ($clients as $key => $client) {  // Minden kliensnél hallgat   
        if (in_array($client, $read)) {
		
			//Mindig megkeresi a hozzátartozó partnert
			if($connection){
		     if ($clients[$key] == $gkey) {
                    $partner = $pkey;
                } else if ($clients[$key] == $pkey) {
                    $partner = $gkey;
                }
			}
			
			//Beolvas
            if (false === ($buf = socket_read($client, 2048, PHP_NORMAL_READ))) {
                echo "socket_read() hiba: " . socket_strerror(socket_last_error($client)) . "\n";
                break 2;
            }
			//Szóközök átugrása
            if (!$buf = trim($buf)) {	
                continue;
            }
			//Kilépés esete
            if ($buf == 'quit') {		
                unset($clients[$key]);
                socket_close($client);
                $grafologus = 0;
                $paciens    = 0; // mindket félt ki kell leptetni MÉG
                echo "Kilepett a {$client} ";
                $kapcsolat = "Megszakadt a kapcsolat!\n";
                socket_write($partner, $kapcsolat, strlen($kapcsolat));
                $clients[$key] = $partner;
                unset($clients[$key]);
                $finish = true;
                break;
            }
			//Chat indítása
            if ($buf == 'chat') {		
			echo "Chat uzenetet kuld az egyik fel.\n";
                $text = "";
                echo "Chat üzenetet fogok kapni\n";
                    if (false === ($text = socket_read($client, 1024))) {
                        echo "socket_read() hiba: " . socket_strerror(socket_last_error($client)) . "\n";
                        break 2;
                    } else {
                        echo " Megkaptam az üzenetet : {$text}\n";
						$chat="chat\n";
						socket_write($partner, $chat, strlen($chat));
                        socket_write($partner, $text, strlen($text));
						
                    }
				echo "Tovabbitottam a chat uzenetet\n";	
				
			//Koordinátákat megkapja a pácienstõl, átküldi a grafológusnak
            } else if ($buf == 'koord' && $client == $pkey) {
			echo "Kapom a koordinatakat a pacienstol...\n";
				$allkoords = "";$koords = "";
				while(strpos($koords, 'end')!=true){
					if (false === ($koords = socket_read($client, 2048, PHP_BINARY_READ))) {	//MÉREEEET
						echo "socket_read() falló: razón2: " . socket_strerror(socket_last_error($client)) . "\n";
						break 2;
					}
					else {		
						$allkoords .= $koords; 
					}
				
				}
			$text="grafologus kap\n";
			socket_write($partner, $text, strlen($text));
			socket_write($partner, $allkoords, strlen($allkoords));
			echo "Megkaptam. Tovabbitottam.Most lementem az adatbazisba.\n";
			$sql="INSERT INTO drawing (data, name) VALUES ('$allkoords', 'kati')";
				if (!mysqli_query($mysqli,$sql))
				{
					die('Error: ' . mysqli_error($mysqli));
				}
				echo "Elkuldve + lementve\n";
				//Lekérdezzük hogy melyik id-ba mentette, és ezt elküldjük
				$id = -8;
				$id = $mysqli->insert_id."\n";
				socket_write($partner, $id, strlen($id));
            }
			else if ($buf == 'Delete' && $client == $gkey){
			echo "Torolni fogok\n";
				if (false === ($id = socket_read($client, 2048, PHP_NORMAL_READ))) {
                echo "socket_read() falló: razón: " . socket_strerror(socket_last_error($client)) . "\n";
                break 2;
                }
				mysqli_query($mysqli,"DELETE FROM drawing WHERE id='$id'");
				echo "Toroltem a {$id}. sort\n";
			}
			 //Bejelentkezik egy grafológus
			else if ($buf == 'grafologus') {		
               /*  if (false === ($name = socket_read($client, 2048, PHP_NORMAL_READ))) {
                echo "socket_read() falló: razón: " . socket_strerror(socket_last_error($client)) . "\n";
                break 2;
                }*/
                $grafologus     = 1;
                $grafszam       = $key;
                $gkey           = $clients[$key];
                $grafologusok[] = $key;
                $grafologusnevek[] = $name;
                echo "Bejelentkezett egy grafologus: {$gkey}\n";
				
				//Bejelentkezik egy páciens
            } else if ($buf == 'paciens') {			
       /*         if (false === ($name = socket_read($client, 2048, PHP_NORMAL_READ))) {
                echo "socket_read() falló: razón: " . socket_strerror(socket_last_error($client)) . "\n";
                break 2;
                }*/
                $paciens     = 1;
                $pacszam     = $key;
                $pkey        = $clients[$key];
        //        $paciensek[] = $clients[$key];
                $paciensnevek[] = $name;
                echo "Bejelentkezett egy paciens: {$pkey}\n";
				
			// Grafológus utasítást küld a páciensnek
            } else if ($connection && $client == $gkey) 
                {
                $buf = "{$buf}\n";
                socket_write($partner, $buf, strlen($buf));
            }
			
			//Létrejött a kapcsolat
            if ($grafologus && $paciens && $grafszam != $pacszam) {
                echo "Kapcsolat!\n";
                $kapcsolat = "Kapcsolat\n";
                foreach ($clients as $key => $client) {
                    socket_write($client, $kapcsolat, strlen($kapcsolat)); 
                }
                $grafologus = 0;
                $paciens    = 0;
                $connection = true;
            }
            //    echo "{$grafologus} ,{$paciens}";
		/*	if($connection){			//kapcsolódáskor kiírja hogy kik léptek be 
				echo " reszt vesznek: ";
				for($i = 0; $i<count($grafologusok); $i++){
				echo "{$grafologusnevek[$i]}";
				socket_write($client, $grafologusnevek[$i], strlen($grafologusnevek[$i]));
				}
			}*/
            
        }
        
    }
} while (true);

socket_close($sock);

?>