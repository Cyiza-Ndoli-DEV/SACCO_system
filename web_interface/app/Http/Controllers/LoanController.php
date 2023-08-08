<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\LoanRequest;
use App\Models\RecommendedList;

class LoanController extends Controller
{
    //
    
    public function approveLoan($applicationNumber)
    {
        // Find the loan request with the given application number
        $loanRequest = LoanRequest::where('applicationNumber', $applicationNumber)->first();
    
        if ($loanRequest) {
            // Update the loan status to 'approved'
            $loanRequest->status = 'approved';
            $loanRequest->save();
            // Update the status of the corresponding recommended loan, if found
        $recommendedLoan = RecommendedList::where('applicationNumber', $applicationNumber)->first();
        if ($recommendedLoan) {
            $recommendedLoan->status = 'granted';
            $recommendedLoan->save();
        }
            session()->flash('success', 'Loan approved successfully!');;
            return redirect()->back();
        } else {
            // Reference not found, set an error flash message
            session()->flash('error', 'Loan not found.');
    
            // Redirect back to the same page with the error message
            return redirect()->back();
        }
    }
}
