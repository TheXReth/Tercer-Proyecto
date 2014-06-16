$( document ).ready(function() {
$('#cssmenu > ul > li > a').click(function() {
  $('#cssmenu li').removeClass('active');
  $(this).closest('li').addClass('active');	
  var checkElement = $(this).next();
  if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
    $(this).closest('li').removeClass('active');
    checkElement.slideUp('normal');
  }
  if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
    $('#cssmenu ul ul:visible').slideUp('normal');
    checkElement.slideDown('normal');
  }
  if($(this).closest('li').find('ul').children().length == 0) {
    return true;
  } else {
    return false;	
  }		
});
});

/*---------------------------------------------------------*/
 $(document).ready(function(){
          $(document.body).on('click', '#Carrito .decrementar',function(e){
e.preventDefault();
    var c = $(this).data("doc_value");
$.ajax({
type: "GET",
          url: 'decrementar',
          data: "doc_reply=" + c,
          success:  function(b) {
          $('#carritoUsuario').html(b);
          }
      });
    });
});

$( document ).ready(function() {
		      $(document.body).on('click', '#Carrito .eliminar',function(e){
e.preventDefault();
		      var c = $(this).data("doc_value");
				$.ajax({
				type: "GET",
		          url: 'eliminar',
		          data: "doc_reply=" + c,
		          success:  function(b) {
		         	 $('#carritoUsuario').html(b);
		          }
		      });
		    });
});
$( document ).ready(function() {
		      $(document.body).on('click', '#Carrito .eliminarCarrito',function(e){
e.preventDefault();
		      var c = $(this).data("doc_value");
				$.ajax({
				type: "GET",
		          url: 'vaciar',
		          data: "doc_reply=" + c,
		          success:  function(b) {
		         	 $('#carritoUsuario').html(b);
		          }
		      });
		    });
});
$( document ).ready(function() {
		      $(document.body).on('click', '.carritoDescripcion',function(e){
e.preventDefault();
		      var c = $(this).data("doc_value");
		$.ajax({
		type: "GET",
			  url: 'carritoAjax',
			  data: "doc_reply=" + c,
			  success:  function(b) {
			  $('#carritoUsuario').html(b);
			  }
		      });
		    });
});

    $(document).ready(function(){
      $(document.body).on('click', '.eliminarComentarioLink', function(e){
      e.preventDefault();
      var c = $(this).data("doc_value");
$.ajax({
type: "GET",
          url: 'eliminarComentario',
          data: "doc_reply=" + c,
          success:  function(b) {
          $('#CajaComentarios').html(b);
          }
      });
    });
});

$( document ).ready(function() {
			$(document.body).on('keyup', '#buscador',function(e){
				e.preventDefault();
				$.ajax({
					type: "GET",
					url: 'buscar',
					data: $('#buscador').serialize(),
					success:  function(data) {
						$('#contenido').html(data);
					}
				});
			});
		});
