@extends('layouts.app', ['activePage' => 'table', 'title' => 'Light Bootstrap Dashboard Laravel by Creative Tim & UPDIVISION', 'navName' => 'Table List', 'activeButton' => 'laravel'])

@section('content')
    <div class="content">
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-12">
                    <div class="card strpied-tabled-with-hover">
                        <div class="card-header ">
                            <h4 class="card-title">Striped Table with Hover</h4>
                            <p class="card-category">Here is a subtitle for this table</p>
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
                                </thead>
                                <tbody>
                                    @foreach($references as $reference)
                                    <tr>
                                        <td>{{ $reference->id }}</td>
                                        <td>{{ $reference->memberNumber }}</td>
                                        <td>{{ $reference->phoneNumber }}</td>
                                        <td>{{ $reference->reason }}</td>
                                        <td>{{ $reference->date }}</td>
                                        <td>
                                        {{ $reference->referenceNumber }}
                                        </td>
                                    </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="col-md-12">
                    <div class="card card-plain table-plain-bg">
                        <div class="card-header ">
                            <h4 class="card-title">Table on Plain Background</h4>
                            <p class="card-category">Here is a subtitle for this table</p>
                        </div>
                        <div class="card-body table-full-width table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <th>ID</th>
                                    <th>Amount</th>
                                    <th>Payment period</th>
                                    <th>Member Number</th>
                                    
                                </thead>
                                <tbody>
                                    @foreach($list as $row)
                                    <tr>
                                        <td>{{ $row->id }}</td>
                                        <td>{{ $row->amount }}</td>
                                        <td>{{ $row->paymentPeriod }}</td>
                                        <td>{{ $row->memberNumber }}</td>
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
@endsection