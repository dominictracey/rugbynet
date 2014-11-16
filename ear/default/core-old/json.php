<?php
$month = isset($_GET['month']) ? $_GET['month'] : date('n');
$year = isset($_GET['year']) ? $_GET['year'] : date('Y');

$array = array(
  array(
    "7/$month/$year", 
    'Yeah you clicked me!', 
    '#', 
    '', 
    'I am event has a click popover'
  ),
  array(
    "17/$month/$year", 
    'Long text', 
    '#', 
    '', 
    'Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation'
  ),
  array(
    "29/$month/$year", 
    'Bootstrap Calendar', 
    '#',
    '',
    'a simple calendar plugin for jQuery and Twitter Bootstrap.'
  )
);

header('Content-Type: application/json');
echo json_encode($array);
exit;
?>