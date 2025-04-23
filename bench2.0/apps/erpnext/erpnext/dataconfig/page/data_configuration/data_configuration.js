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
						<select class="form-control" id="delimiter" required>
							<option value=",">Virgule (,)</option>
							<option value=";">Point-virgule (;)</option>
							<option value="|">Barre verticale (|)</option>
						</select>
					</div>
					<div class="form-group mb-2">
						<label for="csv-file">Csv file</label>
						<input type="file" class="form-control" id="csv-file" accept=".csv" required>
					</div>
					<button type="submit" class="btn btn-primary">Import</button>
				</form>
			</div>
		</div>
	`);
	$(page.body).append(import_section);

	$('#csv-import-form').on('submit', function (e) {
		e.preventDefault();
		const file_input = $('#csv-file')[0];
		const delimiter = $('#delimiter').val();

		if (file_input.files.length === 0) {
			frappe.msgprint("Please, select a CSV file.");
			return;
		}

		const formData = new FormData();
		formData.append("file", file_input.files[0]);
		formData.append("delimiter", delimiter);

		frappe.call({
			method: "chemin.vers.ton.module.import_csv",
			args: {},
			freeze: true,
			freeze_message: "Importation en cours...",
			async: true,
			callback: function (r) {
				frappe.msgprint("Importation terminée !");
			},
			headers: {
				'X-Frappe-CSRF-Token': frappe.csrf_token
			},
			type: 'POST',
			contentType: false,
			processData: false,
			data: formData
		});
	});
};
