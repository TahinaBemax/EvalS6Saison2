frappe.pages['data-configuration'].on_page_load = function(wrapper) {
	const page = frappe.ui.make_app_page({
		parent: wrapper,
		title: 'Data configuration',
		single_column: true
	});

	$("#head").append(`
		<style>
			.loading-overlay {
				display: none;
				position: fixed;
				top: 0;
				left: 0;
				width: 100%;
				height: 100%;
				background-color: rgba(255, 255, 255, 0.95);
				z-index: 9999;
			}

			.loading-spinner {
				position: absolute;
				top: 50%;
				left: 50%;
				transform: translate(-50%, -50%);
				border: 4px solid #f3f3f3;
				border-top: 4px solid #3498db;
				border-radius: 50%;
				width: 40px;
				height: 40px;
				animation: spin 1s linear infinite;
			}

			@keyframes spin {
				0% { transform: rotate(0deg); }
				100% { transform: rotate(360deg); }
			}
		</style>
		`)

	// SECTION 1: Suppression des données
	const delete_section = $(`
		<div class="card mb-4">
			<div class="card-body">
				<h5 class="card-title">Delete data</h5>
				<p>This section will delete all data in the database.Be careful.</p>
				<button class="btn btn-danger" id="delete-data-btn">Reinitialize</button>
			</div>
		</div>
	`);
	$(page.body).append(delete_section);

	// SECTION 2: Importation CSV
	const import_section = $(`
		<div class="card">
			<div class="card-body">
				<h5 class="card-title">Import via csv file</h5>
				<form id="csv-import-form">
					<div class="form-group mb-2">
						<label for="delimiter">Delimitor</label>
						<select class="form-control" name='delimiter' id="delimiter" required>
							<option value=",">Virgule (,)</option>
							<option value=";">Point-virgule (;)</option>
							<option value="|">Barre verticale (|)</option>
						</select>
					</div>
					<div class="form-group mb-2">
						<label for="supplierfile">Supplier file</label>
						<input type="file" class="form-control" name='supplier_file' id="supplierfile" accept=".csv" required>
					</div>
					
					<button type="submit" class="btn btn-primary">Import</button>
				</form>
			</div>
		</div>
	`);
	$(page.body).append(import_section);

	$(page.body).append(`
		<div class="loading-overlay">
    		<div class="loading-spinner"></div>
		</div>`
	)

	// Add this variable at the top of your script
	const $loadingOverlay = $('.loading-overlay');

	// Modify your delete button click handler
	$('#delete-data-btn').on('click', function () {
		frappe.confirm(
			"Are you sure to delete all Data ?",
			() => {
				// Show loader
				$loadingOverlay.show();
				
				frappe.call({
					method: 'erpnext.dataconfig.page.data_configuration.data_configuration.delete_all_data',
					callback: function(response) {
						if (!response.exc) {
							frappe.msgprint("Deleted with success !");
						}
						// Hide loader
						$loadingOverlay.hide();
					}
				});
			}
		);
	});

	// Modify your import form submission
	$('#csv-import-form').on('submit', function (e) {
		e.preventDefault();
		
		// Show loader
		$loadingOverlay.show();
		
		const supplier_file = $('#supplier_file')[0];
		const delimiter = $('#delimiter').val();
		
		if (supplier_file.files.length === 0) {
			frappe.msgprint("Please, select a CSV file.");
			$loadingOverlay.hide(); // Hide loader on error
			return;
		}
		
		const formData = new FormData();
		formData.append("supplier_file", supplier_file.files[0]);
		formData.append("delimiter", delimiter);
		
		$.ajax({
			url: "/api/method/erpnext.dataconfig.page.data_configuration.data_configuration.import_csv",
			type: "POST",
			data: formData,
			processData: false,
			contentType: false,
			headers: {
				'X-Frappe-CSRF-Token': frappe.csrf_token
			},
			success: function (response) {
				const res = response.message;
				if (res.success) {
					frappe.msgprint({
						title: "Data imported",
						message: res.message,
						indicator: "green"
					});
				} else {
					frappe.msgprint({
						title: "Importation failed",
						message: res.message + "<br><br>" + (res.errors || []).join("<br>"),
						indicator: "orange"
					});
				}
				$loadingOverlay.hide(); // Hide loader on completion
			},
			error: function (xhr) {
				let errMsg = "Unknown error.";
				try {
					const res = JSON.parse(xhr.responseText);
					errMsg = res._server_messages
						? JSON.parse(res._server_messages)[0]
						: res.message || xhr.responseText;
				} catch (e) {
					errMsg = xhr.responseText;
				}
				frappe.msgprint({
					title: "Server Error",
					message: errMsg,
					indicator: "red"
				});
				$loadingOverlay.hide(); // Hide loader on error
			}
		});
	});
};
