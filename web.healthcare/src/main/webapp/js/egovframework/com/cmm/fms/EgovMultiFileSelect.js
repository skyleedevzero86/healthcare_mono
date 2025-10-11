/**
 * Convert a single file-input element into a 'multiple' input list
 * Usage:
 *
 *   1. Create a file input element (no name)
 *      eg. <input type="file" id="first_file_element">
 *
 *   2. Create a DIV for the output to be written to
 *      eg. <div id="files_list"></div>
 *
 *   3. Instantiate a MultiSelector object, passing in the DIV and an (optional) maximum number of files
 *      eg. var multi_selector = new MultiSelector( document.getElementById( 'files_list' ), 3 );
 *
 *   4. Add the first element
 *      eg. multi_selector.addElement( document.getElementById( 'first_file_element' ) );
 */

function file_button(){
	$('#sFile').val("Y");	
}


function MultiSelector( list_target, max, fileSeCd ,flag){
	fileSeCd.addEventListener('change', function(event){
		if(event.target.value == ""){
			$.each(event.target.parentNode.childNodes, function (index, value){
				if(value.id == "fileLabel"){
					value.style.backgroundColor  = "#c7c7c7" ;
					$.each(value.childNodes, function (index2, value2){
						if(value2.type == "file"){
							value2.disabled = true;
						}
					});
				}
			});

		}else{
			$.each(event.target.parentNode.childNodes, function (index, value){
				if(value.id == "fileLabel"){
					value.style.backgroundColor  = "#777" ;
					$.each(value.childNodes, function (index2, value2){
						if(value2.type == "file"){
							value2.disabled = false;
						}
					});
				}

			});
		}

    });




	// Where to write the list
	this.list_target = list_target;
	// How many elements?
	this.count = 0;
	// How many elements?
	this.id = 0;
	// Is there a maximum?
	if( max ){
		this.max = max;
	} else {
		this.max = -1;
	};

	this.fileSeCd = fileSeCd ;

	/**
	 * Add a new file input element
	 */
	this.addElement = function( element ){

		// Make sure it's a file input element
		if( element.tagName == 'INPUT' && element.type == 'file' ){

			// Element name -- what number am I?
			element.name = 'file_' + this.id++;

			// Add reference to this object
			element.multi_selector = this;

			// What to do when a file is selected
			element.onchange = function(){

				// New file input
				var new_element = document.createElement( 'input' );
				new_element.type = 'file';


				var nodes = element.parentNode.parentNode.childNodes;
				var selnode = null ;
				for (var i = 0; i < nodes.length; i++) {
					if(nodes.item(i).id == "fileSeCd"+flag){
						if(nodes.item(i).value == ""){
							new_element.disabled = true;
						}
					}
				}


//				if($("#fileSeCd").val() == ""){
//					new_element.disabled = true;
//				}

				// Add new element
				this.parentNode.insertBefore( new_element, this );

				// Apply 'update' to element
				this.multi_selector.addElement( new_element );

				// Update list
				this.multi_selector.addListRow( this );

				// Hide this: we can't use display:none because Safari doesn't like it
				this.style.position = 'absolute';
				this.style.left = '-1000px';
				this.style.top = '-1000px';
				this.style.display = 'none';
				this.style.visibility = 'hidden';
				this.style.width = '0';
				this.style.height = '0';
				this.style.overflow = 'hidden';

				this.parentNode.parentNode.append(this);

				new_element.onkeypress = function(){
					return false;
				};

			};

			// File element counter
			this.count++;
			// Most recent element
			this.current_element = element;

		} else {
			// This can only be applied to file input elements!
			alert( 'Error: not a file input element' );
		};

	};

	/**
	 * Add a new row to the list of files
	 */
	this.addListRow = function( element ){
		// Row div
		var new_row = document.createElement( 'li' );

		// Delete button
		var new_row_button = document.createElement( 'button' );
		new_row_button.type = 'button';
		new_row_button.className = 'delbtn';
		new_row_button.innerText = '삭제';
		new_row_button.id = element.name ;


		// References
		new_row.element = element;

		// Delete function
		new_row_button.onclick= function(){

			// input file tag 삭제
			$(this).parent().parent().parent().parent().children(".file_input").children("input[name='"+this.id+"']").remove();

			// 화면에 표기된 것(선택한 파일. 본인 삭제)
			this.parentNode.parentNode.removeChild( this.parentNode );

			// ㅡmultiselector 카운터 초기화
			this.parentNode.element.multi_selector.count--;

			// Re-enable input element (if it's disabled)
			this.parentNode.element.multi_selector.current_element.disabled = false;


			$(this).parent().parent().parent().parent().children(".file_input").children("#fileLabel").css("background", "#777") ;


			return false;
		};


		var fileName = element.value.replace("C:\\fakepath\\","");


		var nodes = element.parentNode.parentNode.childNodes;

		var selnode = null ;
		for (var i = 0; i < nodes.length; i++) {
			if(nodes.item(i).id == "fileSeCd"+flag){
				selnode = nodes.item(i) ;
			}
		}


		// Set row value
		new_row.innerText = "[ "+ selnode.options[selnode.selectedIndex].text + "] " + fileName ;
		// Add button
		new_row.appendChild( new_row_button );


		/**
		 * selectbox 값 추가
		 * **/

		var new_file_se_sel = document.createElement( 'input' );
		new_file_se_sel.name = "fileSeCd"+flag ;
		new_file_se_sel.type = "hidden" ;
		new_file_se_sel.value = selnode.value ;


		// Add button
		new_row.prepend( new_file_se_sel );


		// Add it to the list
		var nodes2 = this.list_target.childNodes;

		var node = null ;
		for (var i = 0; i < nodes2.length; i++) {
			if(nodes2.item(i).nodeName == "UL"){
				node = nodes2.item(i) ;
			}
		}


		node.appendChild( new_row );

	};

};