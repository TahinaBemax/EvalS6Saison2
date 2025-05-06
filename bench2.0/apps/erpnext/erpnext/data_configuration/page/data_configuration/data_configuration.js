frappe.pages['data_configuration'].on_page_load = function (wrapper) {
	const page = frappe.ui.make_app_page({
		parent: wrapper,
		title: 'Import and reinitialization',
		single_column: true
	});

	// Inject CSS
	$(page.head).append(`
		<style>
			.loading-overlay {
				display: none;
				position: fixed;
				top: 0;
				left: 0;
				width: 100vw;
				height: 100vh;
				background-color: rgba(255, 255, 255, 0.85);
				z-index: 9999;
			}
			.loading-spinner {
				position: absolute;
				top: 50%;
				left: 50%;
				transform: translate(-50%, -50%);
				text-align: center;
				font-family: sans-serif;
			}
			.loading-spinner .spinner {
				border: 6px solid #f3f3f3;
				border-top: 6px solid #007bff;
				border-radius: 50%;
				width: 60px;
				height: 60px;
				animation: spin 1s linear infinite;
				margin: auto;
			}
			.loading-spinner p {
				margin-top: 15px;
				color: #333;
				font-weight: bold;
			}
			@keyframes spin {
				0% { transform: rotate(0deg); }
				100% { transform: rotate(360deg); }
			}
		</style>
	`);

	// Add loading overlay
	const loader = $(`
		<div class="loading-overlay">
			<div class="loading-spinner">
				<div class="spinner"></div>
				<p>Loading...</p>
			</div>
		</div>
	`);
	$(page.body).append(loader);

	const showLoader = () => $('.loading-overlay').fadeIn(200);
	const hideLoader = () => $('.loading-overlay').fadeOut(200);

	// Section suppression
	const deleteSection = $(`
		<div class="card mb-4">
			<div class="card-body">
				<h5 class="card-title">Delete data</h5>
				<p>This section will delete all data in the database. Be careful.</p>
				<button class="btn btn-danger" id="delete-data-btn">Reinitialize</button>
			</div>
		</div>
	`);
	$(page.body).append(deleteSection);

	// Section import CSV
	const importSection = $(`
		<div class="card">
			<div class="card-body">
				<h5 class="card-title">Import via CSV file</h5>
				<form id="csv-import-form">
					<div class="form-group mb-2">
						<label for="delimiter">Delimiter</label>
						<select class="form-control" id="delimiter" required>
							<option value=",">Comma (,)</option>
							<option value=";">Semicolon (;)</option>
							<option value="|">Pipe (|)</option>
						</select>
					</div>

					<div class="form-group mb-2">
						<label for="supplier_file">Supplier file</label>
						<input type="file" class="form-control" id="supplier_file" accept=".csv">
					</div>

					<div class="form-group mb-2">
						<label for="material_request_file">Material Request file</label>
						<input type="file" class="form-control" id="material_request_file" accept=".csv" required>
					</div>

					<div class="form-group mb-2">
						<label for="ref_file">Reference supplier file</label>
						<input type="file" class="form-control" id="ref_file" accept=".csv">
					</div>

					<button type="submit" class="btn btn-primary w-100 my-3">Import</button>
				</form>
			</div>
		</div>
	`);
	$(page.body).append(importSection);

	// Delete data handler
	$('#delete-data-btn').on('click', function () {
		frappe.confirm("Are you sure to delete all Data ?", () => {
			showLoader();
			frappe.call({
				method: 'erpnext.data_configuration.page.data_configuration.data_configuration.delete_all_data',
				callback: function (response) {
					if (!response.exc) {
						frappe.msgprint("Deleted with success!");
					}
					hideLoader();
				}
			});
		});
	});

	// Import CSV handler
	$('#csv-import-form').on('submit', function (e) {
		e.preventDefault();
		showLoader();

		const supplierFile = $('#supplier_file')[0].files[0];
		const ref_file = $('#ref_file')[0].files[0];
		const material_request_file = $('#material_request_file')[0].files[0];
		const delimiter = $('#delimiter').val();

		if (!material_request_file) {
			frappe.msgprint("Please select a CSV file.");
			hideLoader();
			return;
		}

		const formData = new FormData();
		formData.append("supplier_file", supplierFile);
		formData.append("material_request_file", material_request_file);
		formData.append("ref_file", ref_file);
		formData.append("delimiter", delimiter);

		$.ajax({
			url: "/api/method/erpnext.data_configuration.page.data_configuration.data_configuration.import_csv",
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
				hideLoader();
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
				hideLoader();
			}
		});
	});
};
