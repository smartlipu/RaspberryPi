<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta content="text/html; charset=utf-8" />
		<link rel="stylesheet" href="style/jquery-ui.css" />
		<script src="script/jquery-1.9.1.js"></script>
		<script src="script/polling.js"></script>
		<script src="script/jquery-ui.js"></script>
		<link rel="stylesheet" href="style/main.css" />
		<link rel="stylesheet" href="style/bootstrap.min.css" />
		<script src="script/main.js"></script>
	</head>
	<body>
		<div class="heading">
			<h2>Weather Station</h2>
		</div>
		<div class="main">
			<div class="tmp_layout">
				<span class="tmp" id="temp_meter_c-value">--</span>
				<span class="tmp" id="temp_meter_f-value" style="display: none;">--</span>
			</div>
			<div class="tmp_meter"><span class="wob_t" style="display:inline">
				<a href="javascript:void(0)" id="temp_meter_c" class="disabled">&#x2103;</a>
				|
				<a href="javascript:void(0)" id="temp_meter_f">&#8457;</a>
				</span>
			</div>
			<div class="other_meter">
				<div class="time"><!-- <span id="day" style="margin-right:5px;">Sunday</span> --><span id="time">--:--</span></div>
				<div class="hu_meter">Humidity: <span id="wob_hm">-- %</span></div>
			</div>
		</div>
		<div class="main_board">
			<div class="led_collection">
				<div class="on_led brightness_4" id="led1"></div>
				<div class="on_led brightness_4" id="led2"></div>
				<div class="on_led brightness_4" id="led3"></div>
				<div class="on_led brightness_4" id="led4"></div>
				<div class="on_led brightness_4" id="led5"></div>
				<div class="on_led brightness_4" id="led1"></div>
				<div class="on_led brightness_4" id="led2"></div>
				<div class="on_led brightness_4" id="led3"></div>
			</div>
			<div class="action">
				<div id="slider-vertical" style="height: 75px;width:6px"></div>
			</div>
			<div class="action_select">
				<button name="dim" class="btn btn-primary" id="dim" style="display:none;">Dim Effect</button>
				<button name="dim" class="btn btn-primary" id="range">Range Effect</button>
			</div>
		</div>
		<div class="main">
			<div class="sip">
				Sip Url  &nbsp
				<input name="sipurl" class="form-control" placeholder="sip:danish@192.168.1.1">
			</div>
		</div>
	</body>
</html>