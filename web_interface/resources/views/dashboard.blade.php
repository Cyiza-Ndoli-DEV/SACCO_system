@extends('layouts.app', ['activePage' => 'dashboard', 'title' => 'Light Bootstrap Dashboard Laravel by Creative Tim & UPDIVISION', 'navName' => 'Dashboard', 'activeButton' => 'laravel'])

@section('content')
    <!-- Include a div to render the chart -->
    <div style="padding: 50px; width:70%; height:90%; margin:auto;">
        <canvas id="depositChart" ></canvas>
    </div>

    @push('js')

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <script type="text/javascript">
       $(document).ready(function() {
    // Fetch chart data using AJAX
    $.get('{{ route("fetchChartData") }}', function(data) {
    var ctx = document.getElementById('depositChart').getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'bar', // Use 'horizontalBar' for horizontal bar chart
        data: {
            labels: data.labels,
            datasets: [{
                label: 'Total Amount Deposited each month',
                data: data.amounts,
                backgroundColor: 'rgba(0, 128, 0, 1)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }],
        },
        options: { // Additional options can be added here if needed
            scales: {
                x: {
                    beginAtZero: true // Adjust as needed
                }
            }
        }
    });
});

});

    </script>
    @endpush
@endsection
