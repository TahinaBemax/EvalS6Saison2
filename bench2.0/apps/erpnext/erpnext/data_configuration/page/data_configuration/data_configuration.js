frappe.pages['data_configuration'].on_page_load = function (wrapper) {
	const page = frappe.ui.make_app_page({
		parent: wrapper,
		title: 'Import and reinitialization',
		single_column: true
	});


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
						<input type="file" class="form-control" id="supplier_file" accept=".csv" required>
					</div>
					<button type="submit" class="btn btn-primary">Import</button>
				</form>
			</div>
		</div>
	`);
	$(page.body).append(importSection);

	// Delete data handler
	$('#delete-data-btn').on('click', function () {
		frappe.confirm("Are you sure to delete all Data ?", () => {

			frappe.call({
				method: 'erpnext.data_configuration.page.data_configuration.data_configuration.delete_all_data',
				callback: function (response) {
					if (!response.exc) {
						frappe.msgprint("Deleted with success!");
					}
				
				}
			});
		});
	});

	// Import CSV handler
	$('#csv-import-form').on('submit', function (e) {
		e.preventDefault();

		const supplierFile = $('#supplier_file')[0].files[0];
		const delimiter = $('#delimiter').val();

		if (!supplierFile) {
			frappe.msgprint("Please select a CSV file.");
		
			return;
		}

		const formData = new FormData();
		formData.append("supplier_file", supplierFile);
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
			
			}
		});
	});
};
