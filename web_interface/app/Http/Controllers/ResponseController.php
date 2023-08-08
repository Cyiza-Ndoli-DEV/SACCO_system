<?php

namespace App\Http\Controllers;

use App\Models\Reference;
use Illuminate\Http\Request;


class ResponseController extends Controller
{
    //
    public function saveResponse(Request $request)
    {
        try {
            // Get the reference ID from the form
            $referenceId = $request->input('reference_id');
            // Debugging: Check the value of referenceId
    
            // Find the reference by its ID
            $reference = Reference::find($referenceId);
    
            if ($reference) {
                // Get the response text from the form
                $responseText = $request->input('response_text');
    
                // Set the 'response' attribute of the reference to the form value
                $reference->response = $responseText;
    
                // Save the updated reference to the database
                $reference->save();
    
                // Set a success flash message
                session()->flash('success', 'Response saved successfully!');
    
                // Redirect back to the same page with the success message
                return redirect()->back();
            } else {
                // Reference not found, set an error flash message
                session()->flash('error', 'Reference not found.');
    
                // Redirect back to the same page with the error message
                return redirect()->back();
            }
        } catch (\Exception $e) {
            // Log the exception for debugging purposes
    
            // Set an error flash message
            session()->flash('error', 'An error occurred while saving the response.');
    
            // Redirect back to the same page with the error message
            return redirect()->back();
        }
    }
    
    
}
