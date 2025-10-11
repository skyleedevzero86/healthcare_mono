//일반

$('[data-toggle="tooltip"]').tooltip()
$('[data-toggle="popover"]').popover()



// top mega-menu
$(".navbar").hover(function() {
  $('.eb-megamenu').addClass('active');
});
$(".header").mouseleave(function() {
  setTimeout(function() {
    $('.eb-megamenu').removeClass('active');
  }, 150);
});


// https://bootstrap-datepicker.readthedocs.io/en/latest/
$('.date.input-group').datepicker({
  format: 'yyyy-mm-dd',
  todayHighlight: true,
  language: 'ko',
  autoclose: true
});

$('.input-daterange .form-control, .input-daterange .input-group-addon').datepicker({
  format: 'yyyy-mm-dd',
  todayHighlight: true,
  language: 'ko',
  autoclose: true
});