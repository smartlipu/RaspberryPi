
			var temp = null;
			var hum = null;
			var time = null;
			function alterdisable(active,deactivate){
				$("#"+deactivate).addClass('disabled');
				$("#"+active+"-value").hide();
				$("#"+active).removeClass('disabled');
				$("#"+deactivate+"-value").show();
				

			}
			$(document).ready(function(){
				$("#temp_meter_c").bind('click',function(){
					alterdisable("temp_meter_f","temp_meter_c")
				});
				$("#temp_meter_f").bind('click',function(){
					alterdisable("temp_meter_c","temp_meter_f")
				});
				$("#dim").bind('click',function(){
					alterEffect('dim');
				});
				$("#range").bind('click',function(){
					alterEffect('range');
				});
				pollServer();
				pollForever();
				basetemperaturechange(0);
				//alert('okie');
			});
			function alterEffect(effect){
				if(effect == 'dim'){
					$("#dim").hide();
					$("#range").show();
					$.get("livedata?effect=1" , function( data ) {});
				}
				else{
					$("#range").hide();
					$("#dim").show();
					$.get("livedata?effect=0" , function( data ) {});
				}
			}
			function pollServer(){
				//for the polling of the temperature value.
				$.get("livedata" , function( data ) {
					java = data;
					parse(data);
					alterTemplate();
				});
			}
			function pollForever(){
				setInterval(function(){
					pollServer();
				},60000);
				
			}
			function parse(data){
				var jsondata = data;
				var results = jsondata.Results;
				var device;
				var readings;
				var returnval = '';
				
				$.each(results, function(key,value){
					if(value.list == "CUL_HM"){
						device = value.devices;
						readings = device[1].READINGS;
						temp = readings[4].temperature;
						hum = readings[1].humidity;
						time = readings[1].measured;
					}
				});
				//return returnval;
			}
			function alterTemplate(){
				//alter temperature.
				
				$("#temp_meter_c-value").empty().html(temp);
				
				$("#temp_meter_f-value").empty().html(temp*(9/5)+32);
				
				$("#time").empty().html(time.substr(0,16));
				
				$("#wob_hm").empty().html(hum+" %");
			}
			$(function() {
			    $( "#slider-vertical" ).slider({
			      orientation: "vertical",
			      range: "min",
			      min: 0,
			      max: 100,
			      value: 60,
			      slide: function( event, ui ) {
			        basetemperaturechange(ui.value);

			      }
			    });
			    $( "#slider-vertical" ).on( "slidestop", function( event, ui ) {
						$.get( "livedata?value="+ui.value, function( data ) {
						  //$( ".result" ).html( data );
						  //alert( "Load was performed." );
						});
				});
			    
		  	});


		  	function basetemperaturechange(value){
		  		if(value == 0){
		  			$("[id^='led']").removeClass('on_led').addClass('off_led');
		  		}
		  		else{
		  			$("[id^='led']").removeClass('off_led').addClass('on_led');
		  		}

		  		if(value > 0 && value < 25){
					$("[id^='led']").removeClass("brightness_4 brightness_3 brightness_2");
					$("[id^='led']").addClass("brightness_1");
		  		}
		  		else if(value >= 25 && value <=50){
					$("[id^='led']").removeClass("brightness_4 brightness_3 brightness_1");
					$("[id^='led']").addClass("brightness_2");
		  		}
		  		else if(value >50 && value <=75){
					$("[id^='led']").removeClass("brightness_4 brightness_2 brightness_1");
					$("[id^='led']").addClass("brightness_3");
		  		}
		  		else{
					$("[id^='led']").removeClass("brightness_3 brightness_2 brightness_1");
					$("[id^='led']").addClass("brightness_4");
		  		}

		  	}
