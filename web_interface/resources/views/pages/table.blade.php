@extends('layouts.app', ['activePage' => 'table', 'title' => 'Light Bootstrap Dashboard Laravel by Creative Tim & UPDIVISION', 'navName' => 'Table List', 'activeButton' => 'laravel'])

@section('content')
    <div class="content">
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-12">
                    <div class="card strpied-tabled-with-hover">
                        <div class="card-header ">
                            <h4 class="card-title">Pending Requests</h4>
                            <p class="card-category">Here are the pending requests</p>
                        </div>
                        <div class="card-body table-full-width table-responsive">
                            <table class="table table-hover table-striped">
                                <thead>
                                    <th>ID</th>
                                    <th>Member number</th>
                                    <th>Phone number</th>
                                    <th>Reason</th>
                                    <th>Time</th>
                                    <th>Reference Number</th>
                                    <th>Status</th>
                                    <th>Response</th>
                                </thead>
                                <tbody>
    @foreach($references as $reference)
        @php
            // Calculate the time difference in hours between the current time and the reference's date
            $currentTime = now();
            $referenceDate = \Carbon\Carbon::parse($reference->date);
            $hoursDifference = $currentTime->diffInHours($referenceDate);
            
            // Determine if the reference has expired (more than 5 hours)
            $isExpired = $hoursDifference > 5;
        @endphp
        <tr>
            <td>{{ $reference->id }}</td>
            <td>{{ $reference->memberNumber }}</td>
            <td>{{ $reference->phoneNumber }}</td>
            <td>{{ $reference->reason }}</td>
            <td>
                {{ $reference->date }}
            </td>
            <td>{{ $reference->referenceNumber }}</td>
            <td>
                @if ($isExpired)
                    <span class="text-danger">&#9733;</span>
                @endif
            </td>
            <td>
                <button type="button" class="btn btn-primary res-btn" id="resbtn" data-toggle="modal" data-target="#respondModal" data-reference-id="{{ $reference->id }}">Respond</button>
            </td>
        </tr>
    @endforeach
</tbody>


                            </table>
                        </div>
                    </div>
                </div>
                <div class="row">
    <div class="col-md-12">
        <!-- Display success message if it exists -->
        @if (session('success'))
        <div class="alert alert-success">
            {{ session('success') }}
        </div>
        @endif

        <!-- Display error message if it exists -->
        @if (session('error'))
        <div class="alert alert-danger">
            {{ session('error') }}
        </div>
        @endif

        <!-- Rest of your table and content here -->
        <!-- ... -->
    </div>
</div>
                <div class="modal fade" id="respondModal" tabindex="-1" role="dialog" aria-labelledby="uploadModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
        <form action="{{ route('save-response') }}" method="POST" enctype="multipart/form-data">
    @csrf
    <div class="modal-header">
        <h5 class="modal-title" id="uploadModalLabel">Respond to the request</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
    <div class="modal-body">
        <textarea name="response_text" cols="15" rows="5"></textarea>
        <input type="hidden" name="reference_id" id="reference_id" value="">
    </div>
    <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
        <button type="submit" class="btn btn-primary">Save</button>
    </div>
</form>

        </div>
    </div>
</div>
                <div class="col-md-12">
                    <div class="card card-plain table-plain-bg">
                        <div class="card-header ">
                            <h4 class="card-title">Recommanded Loans</h4>
                            <p class="card-category">Waiting to be approved</p>
                        </div>
                        <div class="card-body table-full-width table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <th>ID</th>
                                    <th>Amount</th>
                                    <th>Payment period</th>
                                    <th>Member Number</th>
                                    <th>Application Number</th>
                                    <th>Action</th>
                                </thead>
                                <tbody>
    @foreach($list as $row)
    <tr>
        <td>{{ $row->id }}</td>
        <td>{{ $row->amount }}</td>
        <td>{{ $row->paymentPeriod }}</td>
        <td>{{ $row->memberNumber }}</td>
        <td>{{ $row->applicationNumber }}</td>
        <td>
            <button type="button" class="btn btn-primary grant-btn" data-reference-id="{{ $row->applicationNumber }}">Grant</button>
        </td>
    </tr>
    @endforeach
</tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
   $(document).ready(function() {
    // Use event delegation to handle clicks on buttons with class "res-btn"
    $(document).on('click', '.res-btn', function() {
        var referenceId = $(this).data('reference-id');
        console.log("This is the id: " + referenceId); // Check the value in the browser console
        $('#reference_id').val(referenceId);
    });
});

</script>

<script>
    $(document).ready(function() {
        $('.grant-btn').click(function() {
            var applicationNumber = $(this).data('reference-id');
            alert("Button clicked")
            // Send an AJAX request to update the loan status
            $.ajax({
                type: 'POST',
                url: '/approve-loan/' + applicationNumber,
                data: {
                    _token: '{{ csrf_token() }}'
                },
                success: function(response) {
                    // Handle the success response if needed
                    console.log(response);
                    // Optionally, you can display a success message to the user
                },
                error: function(error) {
                    // Handle the error response if needed
                    console.log(error);
                    // Optionally, you can display an error message to the user
                }
            });
        });
    });
</script>


@endsection