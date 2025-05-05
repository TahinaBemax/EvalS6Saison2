frappe.pages['data-configuration'].on_page_load = function(wrapper) {
	const page = frappe.ui.make_app_page({
		parent: wrapper,
		title: 'Data configuration',
		single_column: true
	});

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

	$('#delete-data-btn').on('click', function () {
		frappe.confirm(
			"Are you sure to delete all Data ?",
			() => {
				frappe.call({
					method: 'erpnext.dataconfig.page.data_configuration.data_configuration.delete_all_data',
					callback: function(response) {
						if (!response.exc) {
							frappe.msgprint("Deleted with success !");
						}
					}
				});
			}
		);
	});

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
						<label for="mat_req_file">Material Request</label>
						<input type="file" class="form-control" name='mat_req_file' id="mat_req_file" accept=".csv" required>
					</div>
					<div class="form-group mb-2">
						<label for="req_for_quota_file">Request for Quotation</label>
						<input type="file" class="form-control" name='req_for_quota_file' id="req_for_quota_file" accept=".csv" required>
					</div>
					
					<button type="submit" class="btn btn-primary">Import</button>
				</form>
			</div>
		</div>
	`);
	$(page.body).append(import_section);

	$('#csv-import-form').on('submit', function (e) {
		e.preventDefault();
		const mat_req_file = $('#mat_req_file')[0];
		const req_for_quota_file = $('#req_for_quota_file')[0];
		const delimiter = $('#delimiter').val();

		if (mat_req_file.files.length === 0) {
			frappe.msgprint("Please, select a CSV file.");
			return;
		}

		const formData = new FormData();
		formData.append("mat_req_file", mat_req_file.files[0]);
		formData.append("req_for_quota_file", req_for_quota_file.files[0]);
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
				// La vraie réponse est dans response.message car Frappe "wrappe" les retours
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
